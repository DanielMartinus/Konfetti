#!/usr/bin/env python3
"""
Compare JMH results of a base commit against a PR head and write a markdown report.

Each side can consist of several JMH JSON files (one per interleaved round). Raw iteration
samples from all files are pooled per benchmark before comparing.

Lower is better for every metric:
- Allocated (gc.alloc.rate.norm, bytes per operation) is nearly deterministic, so any change
  above --alloc-threshold counts.
- Time is noisy on shared CI runners, so a change only counts when it is above
  --time-threshold and the confidence intervals of base and head don't overlap.

Values are medians with a confidence interval derived from the median absolute deviation, so
a few iterations disturbed by other processes on the runner don't move the result.
"""

import argparse
import json
import math
import sys
from pathlib import Path
from statistics import NormalDist, median

ALLOC_METRIC = "gc.alloc.rate.norm"
CONFIDENCE = 0.99

IMPROVED = "🟢"
UNCHANGED = "⚪"
REGRESSED = "🔴"

# Benchmark class name -> (title, description, scenario column, scenario label)
CATEGORIES = {
    "RenderFrameBenchmark": (
        "Per-frame render",
        "one `PartySystem.render()` call",
        "Scenario",
        lambda p: f"{int(p['particles']):,} particles",
    ),
    "LifecycleBenchmark": (
        "Full animation lifecycle",
        "start until `isDoneEmitting()`",
        "Preset",
        lambda p: p["preset"].capitalize(),
    ),
}

NANOS_PER_UNIT = {"ns/op": 1, "us/op": 1e3, "ms/op": 1e6, "s/op": 1e9}


class Stat:
    def __init__(self, samples, unit):
        self.n = len(samples)
        self.value = median(samples)
        self.unit = unit
        if self.n > 1:
            z = NormalDist().inv_cdf(0.5 + CONFIDENCE / 2)
            # 1.4826 * MAD estimates the standard deviation, 1.2533 scales it to the standard error of a median
            sigma = 1.4826 * median(abs(s - self.value) for s in samples)
            self.error = z * 1.2533 * sigma / math.sqrt(self.n)
        else:
            self.error = 0.0

    @property
    def low(self):
        return self.value - self.error

    @property
    def high(self):
        return self.value + self.error


def load(paths):
    """Return {(class, method, params): {"time": Stat, "alloc": Stat}} in order of appearance."""
    samples = {}
    for path in paths:
        for entry in json.loads(Path(path).read_text()):
            key = (entry["benchmark"], tuple(sorted(entry.get("params", {}).items())))
            metrics = samples.setdefault(key, {})
            primary = entry["primaryMetric"]
            add_samples(metrics, "time", primary)
            for name, metric in entry.get("secondaryMetrics", {}).items():
                if name.lstrip("·") == ALLOC_METRIC:
                    add_samples(metrics, "alloc", metric)

    results = {}
    for (benchmark, params), metrics in samples.items():
        class_name, method = benchmark.split(".")[-2:]
        results[(class_name, method, params)] = {
            name: Stat(values, unit) for name, (values, unit) in metrics.items()
        }
    return results


def add_samples(metrics, name, metric):
    values, unit = metrics.setdefault(name, ([], metric["scoreUnit"]))
    for fork in metric["rawData"]:
        values.extend(fork)


def format_time(stat):
    if stat.unit not in NANOS_PER_UNIT:
        return f"{stat.value:,.3g} {stat.unit} ± {stat.error:,.2g}"
    nanos = stat.value * NANOS_PER_UNIT[stat.unit]
    error_nanos = stat.error * NANOS_PER_UNIT[stat.unit]
    for unit, scale in [("s", 1e9), ("ms", 1e6), ("µs", 1e3), ("ns", 1)]:
        if nanos >= scale or unit == "ns":
            return f"{nanos / scale:,.3g} {unit} ± {error_nanos / scale:,.2g}"


def format_bytes(value):
    for unit in ["B", "KB", "MB", "GB"]:
        if abs(value) < 1024 or unit == "GB":
            return f"{value:,.0f} {unit}" if unit == "B" else f"{value:,.1f} {unit}"
        value /= 1024


def format_value(metric, stat):
    return format_time(stat) if metric == "time" else format_bytes(stat.value)


def verdict(metric, base, head, args):
    change = (head.value - base.value) / base.value if base.value else 0.0
    if metric == "alloc":
        significant = abs(change) > args.alloc_threshold
    else:
        overlap = base.low <= head.high and head.low <= base.high
        significant = abs(change) > args.time_threshold and not overlap
    if not significant:
        return change, UNCHANGED
    return change, REGRESSED if change > 0 else IMPROVED


def category_of(class_name):
    return CATEGORIES.get(
        class_name,
        (class_name, None, "Scenario", lambda p: ", ".join(f"{k}={v}" for k, v in p.items()) or "default"),
    )


def build_report(base, head, args):
    has_baseline = base is not None
    base = base or {}
    counts = {IMPROVED: 0, UNCHANGED: 0, REGRESSED: 0}
    alloc_regressions = []
    sections = {}

    keys = list(head) + [k for k in base if k not in head]
    for key in keys:
        class_name, method, params = key
        title, description, column, label = category_of(class_name)
        scenario = label(dict(params))
        rows = sections.setdefault((title, description, column), [])
        head_metrics = head.get(key, {})
        base_metrics = base.get(key, {})

        for metric, metric_label in [("time", "Time"), ("alloc", "Allocated")]:
            b, h = base_metrics.get(metric), head_metrics.get(metric)
            if b is None and h is None:
                continue
            if h is None:
                rows.append([scenario, metric_label, format_value(metric, b), "-", "-", "removed"])
            elif b is None:
                status = "new" if has_baseline else "-"
                rows.append([scenario, metric_label, "-", format_value(metric, h), "-", status])
            else:
                change, mark = verdict(metric, b, h, args)
                counts[mark] += 1
                if metric == "alloc" and mark == REGRESSED:
                    alloc_regressions.append(f"{title} / {scenario}")
                rows.append(
                    [scenario, metric_label, format_value(metric, b), format_value(metric, h), f"{change:+.1%}", mark]
                )

    lines = [f"## Konfetti benchmark: {args.base_label} vs {args.head_label}", ""]
    if has_baseline:
        lines.append(
            f"**Overall:** {counts[REGRESSED]} regressed, {counts[IMPROVED]} improved, {counts[UNCHANGED]} unchanged"
        )
    else:
        lines.append(
            "**No baseline available:** the base commit could not be benchmarked, so only the PR's numbers are shown."
        )
    lines += [
        "",
        f"Legend: {IMPROVED} improved · {UNCHANGED} no significant change · {REGRESSED} regressed",
        f"Allocations: change above {args.alloc_threshold:.0%} is significant. "
        f"Time: change above {args.time_threshold:.0%} with non-overlapping {CONFIDENCE:.0%} confidence intervals.",
        "",
    ]

    category_order = [c[0] for c in CATEGORIES.values()]
    ordered = sorted(
        sections.items(),
        key=lambda s: category_order.index(s[0][0]) if s[0][0] in category_order else len(category_order),
    )
    for (title, description, column), rows in ordered:
        lines.append(f"### {title}" + (f" ({description})" if description else ""))
        lines.append(f"| {column} | Metric | Base | PR | Change | Verdict |")
        lines.append("|---|---|---|---|---|---|")
        lines += ["| " + " | ".join(row) + " |" for row in rows]
        lines.append("")

    if args.details:
        lines += ["<details><summary>Run details</summary>", "", args.details, "", "</details>", ""]

    return "\n".join(lines), alloc_regressions


def parse_args():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--base", nargs="*", default=[], help="JMH JSON files for the base commit")
    parser.add_argument("--head", nargs="+", required=True, help="JMH JSON files for the PR head")
    parser.add_argument("--base-label", default="base")
    parser.add_argument("--head-label", default="PR")
    parser.add_argument("--details", default="", help="Markdown shown in the collapsible run details section")
    parser.add_argument("--alloc-threshold", type=float, default=0.02)
    parser.add_argument("--time-threshold", type=float, default=0.07)
    parser.add_argument("--output", help="Write the report to this file instead of stdout")
    parser.add_argument(
        "--fail-on-alloc-regression",
        action="store_true",
        help="Exit with status 1 when an allocation regression is found",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    base_files = [p for p in args.base if Path(p).is_file()]
    base = load(base_files) if base_files else None
    head = load(args.head)

    report, alloc_regressions = build_report(base, head, args)
    if args.output:
        Path(args.output).write_text(report)
    else:
        print(report)

    if args.fail_on_alloc_regression and alloc_regressions:
        print("Allocation regressions: " + ", ".join(alloc_regressions), file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
