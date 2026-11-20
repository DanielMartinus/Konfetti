"""Draws the README artwork: the logo, the presets and the look at Konfetti Studio.

    pip install fonttools brotli uharfbuzz
    python3 .github/readme/generate.py

The confetti is simulated with the same steps as konfetti-core (gravity, damping, mass, the 2D spin and
the 3D flip) and baked into CSS keyframes, so GitHub plays it in an <img>. Text is turned into paths with
Instrument Sans, Studio's typeface, because an <img> SVG can't load fonts.

The Studio look starts from a frame of the real app: run capture-studio.mjs first when Studio changes.
It also needs ImageMagick.
"""

import base64
import io
import json
import math
import random
import subprocess
from pathlib import Path

import uharfbuzz as hb
from fontTools.pens.recordingPen import DecomposingRecordingPen
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.transformPen import TransformPen
from fontTools.ttLib import TTFont
from fontTools.varLib import instancer

OUT = Path(__file__).resolve().parent
ROOT = OUT.parents[1]
FONT = OUT / "fonts/instrument-sans-latin-wght-normal.woff2"

# Konfetti's default colors, the ones every Party starts with
YELLOW, CORAL, PINK, VIOLET = "#FCE18A", "#FF726D", "#F4306D", "#B48DEF"
COLORS = [YELLOW, CORAL, PINK, VIOLET]

# Studio's stage, in its light and night mode
THEMES = {
    "light": dict(stage="#F1F0F5", dot="#DCDAE4", screen="#FFFFFF", ink="#16151D", muted="#6C6A7A", line="#E3E1EA"),
    "dark": dict(stage="#111016", dot="#2A2833", screen="#1D1C24", ink="#F3F2F7", muted="#A19FB0", line="#2D2B36"),
}


def f(v):
    s = f"{v:.1f}"
    return s[:-2] if s.endswith(".0") else s


# ---------------------------------------------------------------- text


class Font:
    def __init__(self, weight):
        self.tt = instancer.instantiateVariableFont(TTFont(FONT), {"wght": weight})
        self.glyphs = self.tt.getGlyphSet()
        self.order = self.tt.getGlyphOrder()
        self.upm = self.tt["head"].unitsPerEm
        # HarfBuzz can't read woff2, so it gets the decoded instance
        self.tt.flavor = None
        data = io.BytesIO()
        self.tt.save(data)
        self.hb = hb.Font(hb.Face(data.getvalue()))

    def shape(self, text, size, tracking=0.0):
        """Glyphs with their x offsets, kerned. Tracking is in em."""
        buf = hb.Buffer()
        buf.add_str(text)
        buf.guess_segment_properties()
        hb.shape(self.hb, buf, {"kern": True, "liga": True})
        s = size / self.upm
        out, x = [], 0.0
        for info, pos in zip(buf.glyph_infos, buf.glyph_positions):
            out.append((self.order[info.codepoint], x + pos.x_offset * s, text[info.cluster]))
            x += pos.x_advance * s + tracking * size
        return out, x - tracking * size

    def width(self, text, size, tracking=0.0):
        return self.shape(text, size, tracking)[1]

    def path(self, text, size, x, baseline, tracking=0.0, anchor="start"):
        glyphs, w = self.shape(text, size, tracking)
        if anchor == "middle":
            x -= w / 2
        s = size / self.upm
        pen = SVGPathPen(self.glyphs, ntos=f)
        for name, gx, _ in glyphs:
            self.glyphs[name].draw(TransformPen(pen, (s, 0, 0, -s, x + gx, baseline)))
        return pen.getCommands()

    def contours(self, name, size, x, baseline):
        """Each contour of a glyph as its own path, with its top edge, so the dot of an i can be lifted out."""
        rec = DecomposingRecordingPen(self.glyphs)
        self.glyphs[name].draw(rec)
        s = size / self.upm
        shapes, current = [], []
        for op, args in rec.value:
            if op == "moveTo" and current:
                shapes.append(current)
                current = []
            current.append((op, args))
        if current:
            shapes.append(current)
        out = []
        for ops in shapes:
            pen = SVGPathPen(self.glyphs, ntos=f)
            tp = TransformPen(pen, (s, 0, 0, -s, x, baseline))
            pts = []
            for op, args in ops:
                getattr(tp, op)(*args)
                pts += [(x + px * s, baseline - py * s) for px, py in args]
            xs, ys = [p[0] for p in pts], [p[1] for p in pts]
            out.append((pen.getCommands(), (min(xs), min(ys), max(xs), max(ys))))
        return out


# ---------------------------------------------------------------- konfetti


SHAPES = {
    "square": lambda s: f'<rect x="{f(-s / 2)}" y="{f(-s / 2)}" width="{f(s)}" height="{f(s)}"/>',
    "circle": lambda s: f'<circle r="{f(s / 2)}"/>',
    "rect": lambda s: f'<rect x="{f(-s / 2)}" y="{f(-s / 4)}" width="{f(s)}" height="{f(s / 2)}"/>',
    "rect4": lambda s: f'<rect x="{f(-s / 2)}" y="{f(-s / 5)}" width="{f(s)}" height="{f(s * .4)}"/>',
    "star": lambda s: '<path d="M' + " L".join(
        f"{f((s * .62 if i % 2 == 0 else s * .27) * math.cos(-math.pi / 2 + i * math.pi / 5))},"
        f"{f((s * .62 if i % 2 == 0 else s * .27) * math.sin(-math.pi / 2 + i * math.pi / 5))}" for i in range(10)) + 'Z"/>',
    "triangle": lambda s: f'<path d="M0,{f(-s * .55)} L{f(s * .55)},{f(s * .45)} L{f(-s * .55)},{f(s * .45)}Z"/>',
    "diamond": lambda s: f'<path d="M0,{f(-s * .6)} L{f(s * .42)},0 L0,{f(s * .6)} L{f(-s * .42)},0Z"/>',
}
SMALL, MEDIUM, LARGE = (6, 4.0), (8, 5.0), (10, 6.0)


class Party:
    """The Party settings that matter here, in dp and degrees like konfetti-core."""

    def __init__(self, at, position, emitter, angle=0, spread=360, speed=30.0, max_speed=0.0, damping=0.9,
                 sizes=(SMALL, MEDIUM, LARGE), colors=COLORS, shapes=("square", "circle"), ttl=2000):
        self.__dict__.update(locals())


def max_over(duration, amount):
    return [duration * i / amount for i in range(amount)]


def per_second(duration, rate):
    return [i / rate for i in range(int(duration * rate))]


class Stage:
    """Simulates parties on one canvas and bakes every piece into keyframes on a loop of `period` seconds.

    `scale` is how many SVG units one dp takes, `area` is (x, y, w, h) of the canvas in SVG units.
    """

    def __init__(self, period, area, scale, seed, size_scale=1.0, eps=1.0):
        self.period, self.area, self.k, self.size_scale, self.eps = period, area, scale, size_scale, eps
        self.rnd = random.Random(seed)
        self.css, self.svg = [], []
        self.n = 0
        self.prefix = f"k{seed}"
        self.lives = []  # (born, gone) of every piece, to count them

    def start(self, party):
        r = self.rnd
        ax, ay, aw, ah = self.area
        for born in party.emitter:
            born += party.at
            pos = party.position(r) if callable(party.position) else party.position
            x, y = ax + pos[0] * aw, ay + pos[1] * ah
            lo, hi = party.angle - party.spread / 2, party.angle + party.spread / 2
            a = math.radians(party.angle if party.spread == 0 else lo + r.random() * (hi - lo))
            speed = party.speed if party.max_speed == -1 else (party.max_speed - party.speed) * r.random() + party.speed
            vx, vy = speed * math.cos(a), speed * math.sin(a)
            size_dp, mass = r.choice(party.sizes)
            mass += mass * r.random() * 0.2
            spin = 1 + 0.5 * (r.random() * 2 - 1)
            color, shape = r.choice(party.colors), r.choice(party.shapes)
            # One step is 1/60 s, as in Confetti.render
            acc, samples, alpha, life, t = 0.0, [], 255.0, party.ttl / 1000, 0.0
            fade_at = None
            while True:
                samples.append((t, x, y))
                acc += 0.02 / mass
                vy += acc
                x += vx * party.damping * self.k
                y += vy * party.damping * self.k
                vx *= party.damping
                vy *= party.damping
                t += 1 / 60
                if t >= life:
                    fade_at = fade_at or t
                    alpha -= 5
                # Studio starts over at the end of the loop, whatever is still flying
                if alpha <= 0 or y - size_dp * self.k > ay + ah + 4 or born + t >= self.period - 0.01:
                    samples.append((t, x, y))
                    break
            self._bake(born, samples, fade_at, size_dp, spin, color, shape)

    def _bake(self, born, samples, fade_at, size_dp, spin, color, shape):
        end = samples[-1][0]
        fade = None
        if fade_at:
            # The fade starts here, keep a frame for it
            fade = min(range(len(samples)), key=lambda j: abs(samples[j][0] - fade_at))
        keep = _fit(samples, self.eps, fade)
        self.lives.append((born, born + end))
        assert born + end < self.period, f"a piece outlives the loop ({born + end:.2f}s)"
        P = self.period
        name = f"{self.prefix}{self.n:x}"
        self.n += 1

        def pct(t):
            return f"{max(0.0, (born + t) / P * 100):.2f}".rstrip("0").rstrip(".") + "%"

        x0, y0 = samples[0][1], samples[0][2]
        frames = [f"0%,{pct(-0.001)}{{opacity:0;translate:{x0:.0f}px {y0:.0f}px}}"]
        for i, ease in keep:
            t, x, y = samples[i]
            op = f"animation-timing-function:{ease};" if ease else ""
            if i in (0, fade):
                op += "opacity:1;"
            if i == len(samples) - 1:
                # Faded out, or left the stage before its time was up
                op += "opacity:0;" if fade is not None else "opacity:1;"
            frames.append(f"{pct(t)}{{{op}translate:{x:.0f}px {y:.0f}px}}")
        last = samples[-1]
        frames.append(f"100%{{opacity:0;translate:{last[1]:.0f}px {last[2]:.0f}px}}")
        self.css.append(f"@keyframes {name}{{{''.join(frames)}}}")

        size = size_dp * self.k * self.size_scale
        # 8 degrees a step for the 2D spin, and the width shrinks 1.5 px a step for the 3D flip
        turn = 360 / (spin * 8 * 60)
        flip = (size_dp * 2) / (spin * 1.5 * 60) * 2
        start = self.rnd.random()
        body = SHAPES[shape](size).replace("/>", f' fill="{color}"/>', 1)
        # Spin and flip speeds come in steps of 0.05 s, so they can share classes
        turn, flip = max(1, round(turn * 20)), max(1, round(flip * 20))
        self.svg.append(
            f'<g class="c" style="animation:{name} {P:g}s linear infinite">'
            f'<g class="s{turn}" style="animation-delay:-{start * turn / 20:.2f}s">'
            f'<g class="f{flip}" style="animation-delay:-{start * flip / 20:.2f}s">{body}</g></g></g>'
        )

    def style(self):
        return (
            ".c{opacity:0}"
            "@keyframes spin{to{transform:rotate(360deg)}}"
            "@keyframes flip{0%,100%{transform:scaleX(1)}50%{transform:scaleX(0)}}"
            + "".join(f".s{i}{{animation:spin {i / 20:g}s linear infinite}}" for i in range(1, 41))
            + "".join(f".f{i}{{animation:flip {i / 20:g}s linear infinite}}" for i in range(1, 41))
            + "".join(self.css)
        )

    def markup(self):
        return "".join(self.svg)


def _bezier(x1, y1, x2, y2, n=64):
    """A CSS cubic-bezier easing as a lookup from progress in time to progress in place."""
    pts = []
    for k in range(n * 4 + 1):
        u = k / (n * 4)
        pts.append((3 * (1 - u) ** 2 * u * x1 + 3 * (1 - u) * u * u * x2 + u ** 3,
                    3 * (1 - u) ** 2 * u * y1 + 3 * (1 - u) * u * u * y2 + u ** 3))
    table, at = [], 0
    for k in range(n + 1):
        x = k / n
        while at < len(pts) - 1 and pts[at + 1][0] < x:
            at += 1
        (xa, ya), (xb, yb) = pts[at], pts[min(at + 1, len(pts) - 1)]
        table.append(ya if xb == xa else ya + (yb - ya) * (x - xa) / (xb - xa))

    def ease(p):
        q = p * n
        a = min(int(q), n - 1)
        return table[a] + (table[a + 1] - table[a]) * (q - a)

    return ease


# Confetti slows down after a shot and speeds up when it falls, so CSS's own curves fit most of a path
EASINGS = [(None, lambda p: p), ("ease-out", _bezier(0, 0, .58, 1)), ("ease-in", _bezier(.42, 0, 1, 1))]


def _fit(samples, eps, must=None):
    """Keyframes for a path: each one runs as far as a straight line with one of EASINGS stays within eps.

    Returns (index, easing) pairs, the easing being the one from that keyframe to the next.
    """
    out, a, last = [], 0, len(samples) - 1

    def fits(a, b, ease):
        ta, xa, ya = samples[a]
        tb, xb, yb = samples[b]
        for i in range(a + 1, b):
            t, x, y = samples[i]
            e = ease((t - ta) / (tb - ta))
            if abs(x - (xa + (xb - xa) * e)) > eps or abs(y - (ya + (yb - ya) * e)) > eps:
                return False
        return True

    while a < last:
        best = (a + 1, None)
        for name, ease in EASINGS:
            b = a + 1
            while b + 1 <= last and (must is None or not a < must < b + 1 or b + 1 == must) and fits(a, b + 1, ease):
                b += 1
            if b > best[0]:
                best = (b, name)
        out.append((a, best[1]))
        a = best[0]
    out.append((last, None))
    return out


# ---------------------------------------------------------------- svg


REDUCED = "@media (prefers-reduced-motion:reduce){*{animation:none!important}}"


def svg(w, h, body, style, title):
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}" width="{w}" height="{h}" role="img" aria-label="{title}">'
        f"<title>{title}</title><style>{style}{REDUCED}</style>{body}</svg>\n"
    )


def stage(w, h, t, rx=28):
    """Studio's stage: a soft card with a grid of dots."""
    return (
        f'<defs><pattern id="dots" width="22" height="22" patternUnits="userSpaceOnUse">'
        f'<circle cx="11" cy="11" r="1.4" fill="{t["dot"]}"/></pattern>'
        f'<clipPath id="card"><rect width="{w}" height="{h}" rx="{rx}"/></clipPath></defs>'
        f'<rect width="{w}" height="{h}" rx="{rx}" fill="{t["stage"]}"/>'
        f'<rect width="{w}" height="{h}" rx="{rx}" fill="url(#dots)"/>'
    )


def write(name, content):
    (OUT / name).write_text(content)
    print(f"{name:22} {len(content.encode()) // 1024:4} KB")


# ---------------------------------------------------------------- hero


def hero(theme):
    t = THEMES[theme]
    W, H, P = 1200, 520, 8.0
    burst = 1.0
    semibold, medium = Font(600), Font(500)

    size, tracking, baseline = 200, -0.035, 318
    glyphs, word_w = semibold.shape("konfetti", size, tracking)
    x0 = (W - word_w) / 2
    letters, tittle = [], None
    for i, (name, gx, ch) in enumerate(glyphs):
        parts = semibold.contours(name, size, x0 + gx, baseline)
        if ch == "i":
            # The dot of the i is a piece of confetti
            dot = min(parts, key=lambda p: p[1][1])
            tittle = dot[1]
            parts = [p for p in parts if p is not dot]
        d = "".join(p[0] for p in parts)
        letters.append(f'<path class="hop" style="animation-delay:{burst + 0.02 + i * 0.035:.3f}s" d="{d}"/>')

    tx0, ty0, tx1, ty1 = tittle
    side = (tx1 - tx0) * 1.1
    cx, cy = (tx0 + tx1) / 2, (ty0 + ty1) / 2
    piece = (
        f'<g transform="translate({f(cx)} {f(cy)})"><g class="tittle">'
        f'<rect x="{f(-side / 2)}" y="{f(-side / 2)}" width="{f(side)}" height="{f(side)}" rx="{f(side * .14)}" fill="{PINK}"/>'
        f"</g></g>"
    )

    line = medium.path("Android  ·  iOS  ·  Desktop  ·  Web", 30, W / 2, 400, tracking=0.01, anchor="middle")

    k = Stage(P, (0, 0, W, H), 1.3, seed=3, size_scale=1.45, eps=1.2)
    shapes = ("square", "circle", "rect", "square", "circle", "star")
    cannon = dict(spread=34, speed=42, max_speed=62, ttl=2600, shapes=shapes,
                  sizes=((7, 4.0), (9, 5.0), (11, 6.0)))
    k.start(Party(burst, (-0.01, 1.02), max_over(0.15, 52), angle=-60, **cannon))
    k.start(Party(burst, (1.01, 1.02), max_over(0.15, 52), angle=-120, **cannon))
    k.start(Party(burst + 0.04, (cx / W, cy / H), max_over(0.08, 26), speed=4, max_speed=22, ttl=1500,
                  shapes=("square", "circle", "star"), sizes=(SMALL, MEDIUM)))

    def p(s):
        return f"{s / P * 100:.2f}%"

    b = burst
    style = (
        f".word path,.tittle{{transform-box:fill-box;transform-origin:center}}"
        f".hop{{animation:hop {P}s infinite}}"
        f"@keyframes hop{{0%{{transform:translateY(0)}}{p(0.12)}{{transform:translateY(-12px);animation-timing-function:ease-in}}"
        f"{p(0.3)}{{transform:translateY(0);animation-timing-function:ease-out}}{p(0.4)}{{transform:translateY(-3px);animation-timing-function:ease-in}}"
        f"{p(0.5)},100%{{transform:translateY(0)}}}}"
        f".tittle{{animation:tittle {P}s infinite}}"
        f"@keyframes tittle{{0%{{transform:rotate(-8deg) scale(1)}}{p(b - 0.35)}{{transform:rotate(-8deg) scale(1);animation-timing-function:ease-in}}"
        f"{p(b - 0.06)}{{transform:translateY(5px) rotate(-8deg) scale(1.12,.82)}}"
        f"{p(b + 0.1)}{{transform:translateY(-34px) rotate(160deg) scale(1.35)}}"
        f"{p(b + 0.42)}{{transform:translateY(0) rotate(352deg) scale(.94);animation-timing-function:ease-out}}"
        f"{p(b + 0.6)}{{transform:rotate(352deg) scale(1)}}"
        f"{p(4.2)}{{transform:rotate(352deg) scale(1);animation-timing-function:ease-in-out}}"
        f"{p(5.2)},100%{{transform:rotate(352deg) scale(1)}}}}"
        + k.style()
    )
    # Ends where it started: -8 and 352 degrees are the same square
    body = (
        stage(W, H, t)
        + f'<g class="word" fill="{t["ink"]}">{"".join(letters)}</g>'
        + piece
        + f'<path d="{line}" fill="{t["muted"]}"/>'
        + f'<g clip-path="url(#card)">{k.markup()}</g>'
    )
    write(f"hero-{theme}.svg", svg(W, H, body, style, "Konfetti. The dot of the i jumps and confetti bursts from both corners."))


# ---------------------------------------------------------------- presets


def presets(theme):
    """The four presets from the samples, each on its own screen, with the settings they use."""
    t = THEMES[theme]
    W, H, P = 1200, 500, 7.6
    semibold, medium = Font(600), Font(500)
    tw, th, gap = 258, 352, 32
    x_first = (W - 4 * tw - 3 * gap) / 2
    top = 36
    k = 0.7  # 258 units is a 368 dp wide screen
    names = [("Explode", "spread = 360"), ("Festive", "four parties at once"),
             ("Parade", "from both sides"), ("Rain", "Position.between(...)")]

    css, screens, clips = [], [], []
    for i, (name, note) in enumerate(names):
        x = x_first + i * (tw + gap)
        clips.append(f'<clipPath id="s{i}"><rect x="{f(x)}" y="{top}" width="{tw}" height="{th}" rx="26"/></clipPath>')
        st = Stage(P, (x, top, tw, th), k, seed=20 + i, size_scale=1.6, eps=1.3)
        if name == "Explode":
            st.start(Party(0.4, (0.5, 0.3), max_over(0.1, 60), speed=0, max_speed=30))
            st.start(Party(3.4, (0.5, 0.3), max_over(0.1, 40), speed=0, max_speed=26))
        elif name == "Festive":
            base = dict(angle=-90, damping=0.9, sizes=(SMALL, LARGE, LARGE), ttl=3000)
            for spd, mx, spr, n in ((30, 50, 45, 26), (55, 65, 10, 9), (50, 60, 120, 30), (65, 80, 10, 9)):
                st.start(Party(0.9, (0.5, 1.0), max_over(0.1, n), speed=spd * 0.82, max_speed=mx * 0.82, spread=spr, **base))
        elif name == "Parade":
            st.start(Party(0.3, (0.0, 0.5), per_second(4.0, 9), angle=-45, spread=30, speed=10, max_speed=30, ttl=2000))
            st.start(Party(0.3, (1.0, 0.5), per_second(4.0, 9), angle=-135, spread=30, speed=10, max_speed=30, ttl=2000))
        else:
            st.start(Party(0.3, lambda r: (r.random(), 0.0), per_second(4.0, 22), angle=90, spread=360, speed=0, max_speed=15, ttl=2000))
        css.append(st.style())
        screens.append(
            f'<rect x="{f(x)}" y="{top}" width="{tw}" height="{th}" rx="26" fill="{t["screen"]}" stroke="{t["line"]}" stroke-width="1.5"/>'
            f'<g clip-path="url(#s{i})">{st.markup()}</g>'
            f'<path d="{semibold.path(name, 25, x + tw / 2, top + th + 50, tracking=-0.01, anchor="middle")}" fill="{t["ink"]}"/>'
            f'<path d="{medium.path(note, 17, x + tw / 2, top + th + 78, anchor="middle")}" fill="{t["muted"]}"/>'
        )
    # Every stage brings the same spin and flip keyframes, once is enough
    shared = css[0].split("@keyframes k")[0]
    style = shared + "".join("@keyframes k" + c.split("@keyframes k", 1)[1] for c in css)
    body = stage(W, H, t) + f"<defs>{''.join(clips)}</defs>" + "".join(screens)
    write(f"presets-{theme}.svg", svg(W, H, body, style, "Four presets playing: Explode, Festive, Parade and Rain."))


# ---------------------------------------------------------------- studio


def studio():
    """Konfetti Studio playing its starter project, from a frame capture-studio.mjs took of the real app.

    The confetti, the playhead and the two readouts move together on Studio's own loop.
    """
    src = OUT / "studio"
    L = json.loads((src / "layout.json").read_text())
    W, H = L["viewport"]
    P = L["length"]

    # The starter project, as in konfetti.studio src/model/presets.ts
    cannon = dict(spread=40, speed=35, max_speed=55, ttl=2000, sizes=((10, 5.0),),
                  colors=["#FF726D", "#FCE18A", "#36C5F0", "#2EB67D", "#B48DEF"], shapes=("rect4", "star"))
    css, layers, clips, counts = [], [], [], None
    for i, s in enumerate(L["screens"]):
        wdp, hdp = s["dp"]
        k = s["w"] / wdp
        clips.append(f'<clipPath id="sc{i}"><rect x="{f(s["x"])}" y="{f(s["y"])}" width="{f(s["w"])}" height="{f(s["h"])}" rx="{f(s["radius"])}"/></clipPath>')
        st = Stage(P, (s["x"], s["y"], s["w"], s["h"]), k, seed=40 + i, eps=1.8)
        st.start(Party(0.0, (24 / wdp, 600 / hdp), max_over(0.2, 80), angle=-45, **cannon))
        st.start(Party(0.4, (0.95, 0.75), max_over(0.2, 80), angle=-135, **cannon))
        st.start(Party(0.8, lambda r: (r.random(), 0.0), per_second(2.5, 60), angle=90, spread=360, speed=0, max_speed=10,
                       ttl=4000, colors=[PINK, VIOLET], shapes=("circle", "diamond")))
        css.append(st.style())
        layers.append(f'<g clip-path="url(#sc{i})">{st.markup()}</g>')
        if counts is None:
            counts = st.lives  # the first screen is the active one, its pieces are the ones Studio counts
    shared = css[0].split("@keyframes k")[0]
    style = shared + "".join("@keyframes k" + c.split("@keyframes k", 1)[1] for c in css)

    def pct(t):
        return f"{t / P * 100:.3f}".rstrip("0").rstrip(".") + "%"

    # The playhead, swept across the lanes once a loop
    ph = L["playhead"]
    playhead = (
        f'<g class="ph"><rect x="{f(ph["x"] - 0.75)}" y="{f(ph["top"] + 14)}" width="1.5" height="{f(ph["bottom"] - ph["top"] - 14)}" fill="{PINK}"/>'
        f'<circle cx="{f(ph["x"])}" cy="{f(ph["top"] + 15)}" r="6" fill="{PINK}" stroke="#fff" stroke-width="2"/></g>'
    )
    style += f".ph{{animation:ph {P}s linear infinite}}@keyframes ph{{to{{transform:translateX({f(ph['w'])}px)}}}}"

    # Readouts: each digit is a strip of 0-9 behind a window the size of one character
    digits, n = [], 0

    def digit(cell, strip_png, strip, frames):
        nonlocal n
        n += 1
        cw = strip["w"] / 10
        y = cell["y"] + (cell["h"] - strip["h"]) / 2
        keys = "".join(
            f"{pct(t)}{{transform:translateX({f(-d * cw if d is not None else 0)}px);opacity:{0 if d is None else 1}}}" for t, d in frames
        )
        style_ = f"@keyframes d{n}{{{keys}}}.d{n}{{animation:d{n} {P}s steps(1,end) infinite}}"
        digits.append(
            f'<clipPath id="dc{n}"><rect x="{f(cell["x"])}" y="{f(cell["y"])}" width="{f(cw)}" height="{f(cell["h"])}"/></clipPath>'
            f'<g clip-path="url(#dc{n})"><use class="d{n}" href="#{strip_png}" x="{f(cell["x"])}" y="{f(y)}"/></g>'
        )
        return style_

    step = 0.05
    ticks = [round(i * step, 3) for i in range(int(P / step) + 1) if i * step < P]
    time_cells = [c for c in L["time"] if c["c"].isdigit()]  # 0.00: ones, tenths, hundredths
    ones = [(t, int(t)) for t in ticks]
    tenths = [(t, int(round(t * 100)) // 10 % 10) for t in ticks]
    hundredths = [(t, int(round(t * 100)) % 10) for t in ticks]
    for cell, frames in zip(time_cells, (ones, tenths, hundredths)):
        style += digit(cell, "strip-time", L["timeStrip"], _changes(frames))

    def alive(t):
        return sum(1 for born, gone in counts if born <= t < gone)

    values = [(t, alive(t)) for t in ticks]
    for place, cell in zip((100, 10, 1), L["count"]):
        frames = [(t, (v // place) % 10 if v >= place or place == 1 else None) for t, v in values]
        style += digit(cell, "strip-count", L["countStrip"], _changes(frames))

    base = _b64(["magick", str(src / "base.png"), "-resize", "1920x", "-strip", "-quality", "80", "jpeg:-"])
    handles = _b64(["magick", str(src / "handles.png"), "-strip", "-colors", "128", "png8:-"])
    strips = "".join(
        f'<image id="strip-{name}" href="data:image/png;base64,{_b64(["magick", str(src / f"digits-{name}.png"), "-strip", "png:-"])}" '
        f'width="{f(L[f"{name}Strip"]["w"])}" height="{f(L[f"{name}Strip"]["h"])}"/>'
        for name in ("time", "count")
    )
    body = (
        f'<defs><clipPath id="frame"><rect width="{W}" height="{H}" rx="14"/></clipPath>{"".join(clips)}{strips}</defs>'
        f'<g clip-path="url(#frame)"><image href="data:image/jpeg;base64,{base}" width="{W}" height="{H}"/>'
        + "".join(layers)
        + f'<image href="data:image/png;base64,{handles}" width="{W}" height="{H}"/>'
        + playhead
        + "".join(digits)
        + f'</g><rect x=".5" y=".5" width="{W - 1}" height="{H - 1}" rx="13.5" fill="none" stroke="#16151D" stroke-opacity=".12"/>'
    )
    write("studio.svg", svg(W, H, body, style, "Konfetti Studio playing its starter project on an iPhone and a Pixel side by side."))


def _changes(frames):
    """Only the moments a digit changes, plus the first one."""
    out = []
    for t, d in frames:
        if not out or out[-1][1] != d:
            out.append((t, d))
    return out


def _b64(cmd):
    return base64.b64encode(subprocess.run(cmd, check=True, capture_output=True).stdout).decode()


# ---------------------------------------------------------------- mark


def mark():
    """The four pieces of the mark, each floating on its own."""
    style = (
        ".m{transform-box:fill-box;transform-origin:center}"
        "@keyframes a{50%{transform:translate(0,-1.2px) rotate(-14deg)}}"
        "@keyframes b{50%{transform:translate(.6px,1px) scale(.82)}}"
        "@keyframes c{50%{transform:translate(-.8px,.6px) rotate(10deg)}}"
        "@keyframes d{50%{transform:translate(.4px,-1px) rotate(24deg)}}"
        ".a{animation:a 3.1s ease-in-out infinite}.b{animation:b 2.6s ease-in-out infinite}"
        ".c2{animation:c 3.5s ease-in-out infinite}.d{animation:d 2.9s ease-in-out infinite}"
    )
    body = (
        f'<g class="m a"><rect x="2" y="3" width="5" height="5" rx="1" fill="{YELLOW}" transform="rotate(-15 4 5)"/></g>'
        f'<circle class="m b" cx="13" cy="5" r="2.6" fill="{CORAL}"/>'
        f'<rect class="m c2" x="3" y="11" width="6" height="3" rx="1" fill="{VIOLET}"/>'
        f'<polygon class="m d" points="13,10 16,15 10,15" fill="{PINK}"/>'
    )
    content = (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 18 18" width="72" height="72" role="img" aria-label="Konfetti">'
        f"<title>Konfetti</title><style>{style}{REDUCED}</style>{body}</svg>\n"
    )
    write("mark.svg", content)


if __name__ == "__main__":
    for theme in THEMES:
        hero(theme)
        presets(theme)
    studio()
    mark()
