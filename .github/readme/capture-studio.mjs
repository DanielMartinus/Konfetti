// Captures Konfetti Studio for the README: the app with time frozen at the start, so no confetti
// is on the screens yet, plus what generate.py needs to play the starter project on top of it.
//
//   node .github/readme/capture-studio.mjs
//
// Needs Playwright (the one in konfetti.studio/films/studio-demo works, see PLAYWRIGHT below).
// Writes .github/readme/studio/: base.png, handles.png, digits-time.png, digits-count.png, layout.json
import { createRequire } from 'node:module';
import { existsSync, mkdirSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const PLAYWRIGHT = process.env.PLAYWRIGHT ?? `${process.env.HOME}/Documents/dionsegijn/konfetti.studio/films/studio-demo/package.json`;
const { chromium } = createRequire(PLAYWRIGHT)('playwright');

const URL = 'https://danielmartinus.github.io/konfetti.studio/';
const OUT = join(dirname(fileURLToPath(import.meta.url)), 'studio');
const VW = 1440;
const VH = 810;
const DSF = 2;

mkdirSync(OUT, { recursive: true });
// Any Chrome works, by default the one the Studio film uses
const CHROME = process.env.CHROME ?? `${process.env.HOME}/Library/Caches/ms-playwright/chromium-1234/chrome-mac-arm64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing`;
const browser = await chromium.launch({ executablePath: existsSync(CHROME) ? CHROME : undefined });
const ctx = await browser.newContext({ viewport: { width: VW, height: VH }, deviceScaleFactor: DSF, colorScheme: 'light' });
const page = await ctx.newPage();
// No animation frames ever run, so playback stays at 0 s and the screens stay empty
await page.addInitScript(() => {
  window.requestAnimationFrame = () => 0;
  window.cancelAnimationFrame = () => {};
});
await page.goto(URL, { waitUntil: 'networkidle' });
await page.evaluate(() => document.fonts.ready);
await page.waitForTimeout(800);

const layout = await page.evaluate(() => {
  const rect = (el) => {
    const r = el.getBoundingClientRect();
    return { x: r.x, y: r.y, w: r.width, h: r.height };
  };
  const screens = [...document.querySelectorAll('canvas')].map((canvas) => {
    const frame = canvas.parentElement;
    const label = frame.parentElement.querySelector('.mono').textContent; // 393×852 · 3x
    const [w, h] = label.split('·')[0].trim().split('×').map(Number);
    return { ...rect(canvas), radius: parseFloat(getComputedStyle(frame).borderTopLeftRadius), dp: [w, h] };
  });

  // The playhead: a line and a dot, inside the lanes area
  const line = [...document.querySelectorAll('div')].find((d) => d.style.left === '-0.75px');
  const head = line.parentElement;
  const lanes = rect(head.offsetParent);
  const playhead = { x: lanes.x, w: lanes.w, top: rect(head).y, bottom: rect(head).y + rect(head).h };
  head.style.display = 'none';

  // Character cells of a text node, so digits can be swapped in place
  const cells = (node) =>
    [...node.textContent].map((c, i) => {
      const range = document.createRange();
      range.setStart(node, i);
      range.setEnd(node, i + 1);
      return { c, ...rect(range) };
    });

  const muted = [...document.querySelectorAll('span.mono > span.muted')].find((s) => s.textContent.startsWith('/'));
  const timeSpan = muted.parentElement;
  const length = parseFloat(muted.textContent.replace('/', ''));
  const timeNode = timeSpan.firstChild;
  const time = cells(timeNode);
  timeNode.textContent = timeNode.textContent.replace(/\d/g, ' ');

  const countSpan = [...document.querySelectorAll('span.mono.muted')].find((s) => s.textContent.endsWith('pieces'));
  countSpan.textContent = '000 pieces';
  const count = cells(countSpan.firstChild).slice(0, 3);
  countSpan.textContent = '    pieces';

  // Strips of 0-9 in the same type as both readouts
  const strip = (from, id) => {
    const s = document.createElement('span');
    const cs = getComputedStyle(from);
    for (const p of ['fontFamily', 'fontSize', 'fontWeight', 'color', 'letterSpacing', 'fontVariantNumeric']) s.style[p] = cs[p];
    Object.assign(s.style, { position: 'fixed', left: '0px', top: id === 'time' ? '0px' : '40px', lineHeight: '20px', whiteSpace: 'pre', zIndex: 99999, background: 'transparent' });
    s.id = `strip-${id}`;
    s.textContent = '0123456789';
    s.style.visibility = 'hidden';
    document.body.appendChild(s);
    return rect(s);
  };
  const timeStrip = strip(timeSpan, 'time');
  const countStrip = strip(countSpan, 'count');
  return { viewport: [innerWidth, innerHeight], screens, playhead, length, time, count, timeStrip, countStrip };
});

await page.screenshot({ path: join(OUT, 'base.png') });

// The emitter handles on their own, to draw over the confetti like Studio does
await page.addStyleTag({ content: 'html,body,#root,#root *{visibility:hidden!important;background:transparent!important;box-shadow:none!important}' });
// The emitter handles are the svgs next to each screen's canvas, the icons in the panels stay hidden
await page.evaluate(() => {
  for (const svg of document.querySelectorAll('#root svg')) {
    if (!svg.parentElement.querySelector(':scope > div > canvas')) continue;
    for (const el of [svg, ...svg.querySelectorAll('*')]) el.style.setProperty('visibility', 'visible', 'important');
  }
});
await page.screenshot({ path: join(OUT, 'handles.png'), omitBackground: true });

for (const id of ['time', 'count']) {
  await page.evaluate((id) => document.getElementById(`strip-${id}`).style.setProperty('visibility', 'visible', 'important'), id);
  const r = layout[`${id}Strip`];
  await page.screenshot({ path: join(OUT, `digits-${id}.png`), omitBackground: true, clip: { x: r.x, y: r.y, width: r.w, height: r.h } });
  await page.evaluate((id) => document.getElementById(`strip-${id}`).style.setProperty('visibility', 'hidden', 'important'), id);
}

writeFileSync(join(OUT, 'layout.json'), JSON.stringify(layout, null, 2));
await browser.close();
console.log(JSON.stringify(layout.screens), layout.length);
