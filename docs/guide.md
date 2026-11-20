# Konfetti guide

Everything a `Party` can do, and how to show it. New here? Start with the [README](../README.md#get-started).

- [Party options](#party-options)
- [Shapes](#shapes): polygons, [streamers](#streamers) and [your own image](#your-own-image)
- [KonfettiView](#konfettiview): [when a party ends](#when-a-party-ends), [in front of or behind other views](#in-front-of-or-behind-other-views), [remove animations](#remove-animations), [Konfetti Studio files](#konfetti-studio-files)

## Party options

- `Angle` - **Int (default: 0)**: The direction the confetti will shoot. Use any integer between `0-360` or use presets like: Angle.TOP, Angle.RIGHT, Angle.BOTTOM, Angle.LEFT. Angle.RIGHT equates to `0` degrees, and larger values move clockwise from this position.
- `spread` - **Int (default: 360)**: How wide the confetti will shoot in the direction of Angle. Use any integer between `0-360`. Use 1 to shoot in a straight line and 360 to form a circle
- `speed` - **Float (default: 30f)**: The start speed of the confetti at the time of creation.
- `maxSpeed` - **Float (default: 0f)**: A random speed between `speed` and `maxSpeed` will be chosen. With the default of 0 that's a speed between 0 and `speed`. Set to -1 to always use `speed`. Using randomness creates a more natural and realistic look to the confetti when animating.
- `damping` - **Float (default: 0.9f)**: The rate at which the speed will decrease right after shooting the confetti
- `size` - **`List<Size>` (default: SMALL, MEDIUM, LARGE)**: The size of the confetti. Use: Size.SMALL, MEDIUM or LARGE for default sizes or
 create your custom size using a new instance of `Size`.
- `colors` - **`List<Int>` (default: 0xfce18a, 0xff726d, 0xf4306d, 0xb48def)**: List of colors that will be randomly picked from
- `shapes` - **`List<Shape>` (default: Shape.Square, Shape.Circle)**: Also `Shape.Rectangle`, polygons like `Shape.Polygon.STAR`, streamers with `Shape.Ribbon` (see [Shapes](#shapes)) or your own image with `Shape.DrawableShape`
- `timeToLive` - **Long (default: 2000)**: The time in milliseconds a particle will stay alive after that the confetti will disappear.
- `fadeOutEnabled` - **Boolean (default: true)**: If true and a confetti disappears because it ran out of time (set with timeToLive) it will slowly fade out. If set to false it will instantly disappear from the screen.
- `position` - **Position (default: Position.Relative(0.5, 0.5))**: the location where the confetti will spawn from relative to the canvas. Use absolute
 coordinates with `[Position.Absolute]` or relative coordinates between 0.0 and 1.0 using `[Position.Relative]`. Spawn confetti between random locations using `[Position.between]`.
 - `delay` - **Int (default: 0)**: the amount of milliseconds to wait before the rendering of the confetti starts
 - `rotation` - **Rotation (default: Rotation)**: enable the 3D rotation of a Confetti. See [Rotation] class for the configuration
 options. Easily enable or disable it using [Rotation].enabled() or [Rotation].disabled() and control the speed of rotations.
 Set `Rotation(faceDirection = true)` to turn each confetti the way it's moving, like the sparks of a firework. Works best with a thin `Shape.Rectangle(0.2f)`.
 - `drift` - **Drift (default: off)**: lets the confetti sway from side to side while it falls, like paper drifting down. Turn it on with `Drift.enabled()`, or set how far (`sway`, in dp), how often (`frequency`), how much it leans (`tilt`) and how slow it falls (`drag`).
 - `emitter` - **EmitterConfig**: Instructions how many and how often a confetti particle should spawn. Use `Emitter(duration).max(amount)` or `Emitter(duration).perSecond(amount)` to configure the Emitter, for example `Emitter(5.seconds).perSecond(30)`. 

See Party implementation [here](/konfetti/core/src/commonMain/kotlin/nl/dionsegijn/konfetti/core/Party.kt)

## Shapes

Next to squares, circles and rectangles you can use polygons. They spin, flip and fade just like the other shapes. Three come built in:

```kotlin
shapes = listOf(Shape.Polygon.STAR, Shape.Polygon.TRIANGLE, Shape.Polygon.DIAMOND)
```

Want a different star? `Shape.Polygon.star(points = 6, innerRadius = 0.5f)` makes one with 6 points and wider arms.

You can also draw your own polygon. Each point is a fraction of the confetti's square, where (0, 0) is the top-left corner and (1, 1) the bottom-right one:

```kotlin
val arrow = Shape.Polygon(
    Shape.Polygon.Point(0.5f, 0f),
    Shape.Polygon.Point(1f, 0.6f),
    Shape.Polygon.Point(0.5f, 0.4f),
    Shape.Polygon.Point(0f, 0.6f),
)
```

Stars take a bit more work to draw than the other shapes, so for very big parties keep them in the mix rather than using only stars. The confetti's `Size` sets how big the square is, so a star might look better a size larger than your squares.

### Streamers

`Shape.Ribbon` is a curly paper streamer. It hangs from its head and spins around its own length while it falls, instead of tumbling like flat confetti:

```kotlin
shapes = listOf(Shape.Ribbon(minLength = 60f, maxLength = 120f))
```

Each streamer picks a length between `minLength` and `maxLength` (in dp). `spin` sets how many turns a second it spins and `variance` how different the streamers are from each other. The confetti's `Size` sets how wide the curl is. Turn on `drift` to let them sway, the body then trails behind the head.

### Your own image

Images work as confetti too. By default they get the confetti colors on top and fade out at the end. Turn that off to keep the image as it is.

With Compose, on every platform, use an `ImageBitmap` or a `Painter`, for example from Compose resources:

```kotlin
val heart = imageShape(painterResource(Res.drawable.heart))
val logo = imageShape(imageBitmap, tint = false)
```

On Android you can also use any drawable:

```kotlin
// Colored like the rest of the confetti
val heart = ImageUtil.loadDrawable(drawable)
// Original colors, still fades out
val logo = ImageUtil.loadDrawable(drawable, tint = false)
// Exactly as it is
val photo = ImageUtil.loadDrawable(drawable, tint = false, applyAlpha = false)
```

Then add it to the shapes of your party, for example `shapes = listOf(Shape.Square, logo)`.

## KonfettiView

Create a `KonfettiView` in your compose UI or add one to your xml layout depending on your setup.

Compose
```kotlin
val konfetti = rememberKonfettiState()

Button(onClick = { konfetti.start(party) }) {
    Text("Celebrate")
}

KonfettiView(
    state = konfetti,
    modifier = Modifier.fillMaxSize(),
)
```

`start` adds a party on top of whatever is already running, so you can call it as often as you like. Some other things you can do with the state:

```kotlin
val started = konfetti.burst(at = offset, party = party) // start a party where the user tapped, in pixels
konfetti.stop(started)                                   // take that burst back
konfetti.stop(party)                                     // remove one party right away
konfetti.stopGracefully()                                // stop spawning and let the confetti finish
konfetti.reset()                                         // remove all confetti right away
```

To shoot confetti from a button or a row, let `KonfettiView` fill the screen and use the center of the button in the root:

```kotlin
var center by remember { mutableStateOf(Offset.Zero) }
Checkbox(
    checked = done,
    onCheckedChange = { konfetti.burst(at = center, party = party) },
    modifier = Modifier.onGloballyPositioned { center = it.boundsInRoot().center },
)
```

Want to know when it's done? See [When a party ends](#when-a-party-ends).

If you only need confetti when a screen shows up, you can still pass the parties directly. `KonfettiView` starts over whenever the list changes, so `remember` your parties (and drawables) to avoid restarting on every recomposition:

```kotlin
val parties = remember { listOf(party) }
KonfettiView(modifier = Modifier.fillMaxSize(), parties = parties)
```

View-based (xml)
```xml
<nl.dionsegijn.konfetti.xml.KonfettiView
    android:id="@+id/konfettiView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
Party(
    speed = 0f,
    maxSpeed = 30f,
    damping = 0.9f,
    spread = 360,
    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
    emitter = Emitter(100.milliseconds).max(100),
    position = Position.Relative(0.5, 0.3)
)
viewKonfetti.start(party)
```

### When a party ends

Want to hide the view or move on once the confetti is done? You get a call for every party that finishes, with the number of parties still running. When that's 0, everything is done.

```kotlin
// Compose
KonfettiView(
    state = konfetti,
    modifier = Modifier.fillMaxSize(),
    onPartyEnded = { activeParties ->
        if (activeParties == 0) showNextScreen()
    },
)

// View-based (xml)
konfettiView.onParticleSystemUpdateListener = object : OnParticleSystemUpdateListener {
    override fun onParticleSystemStarted(view: KonfettiView, party: Party, activeSystems: Int) {}

    override fun onParticleSystemEnded(view: KonfettiView, party: Party, activeSystems: Int) {
        if (activeSystems == 0) showNextScreen()
    }
}
```

### In front of or behind other views

Konfetti draws in the order of your layout, like any other view or composable.

- In Compose, the composable that comes later in a `Box` draws on top. Put `KonfettiView` last to show confetti in front of everything, or first to keep it behind. `Modifier.zIndex()` changes the order without moving code around.
- In XML, a view later in the layout draws on top. Views with elevation, like buttons and cards, always draw above views without it, no matter where they are in the layout. To get confetti in front of those too, give the `KonfettiView` a higher `android:elevation` or `android:translationZ`.

### Remove animations

Some people turn on "Remove animations" (Android) or "Reduce Motion" (iOS and the browser) in their settings, for example because motion makes them feel sick. Konfetti doesn't change anything by itself, but you can check the setting and skip the confetti or show something calmer instead:

```kotlin
// Compose, on desktop this is always false
val reduceMotion = rememberReduceMotion()
Button(onClick = { if (!reduceMotion) konfetti.start(party) }) { Text("Celebrate") }

// View-based (xml)
if (!KonfettiView.isReduceMotionEnabled(context)) konfettiView.start(party)
```

### Konfetti Studio files

[Konfetti Studio](https://danielmartinus.github.io/konfetti.studio/) exports parties as json. Read them with `konfetti-json`:

```kotlin
implementation("nl.dionsegijn:konfetti-json:3.0.0")

val parties = KonfettiJson.decode(json, density = LocalDensity.current.density)
konfetti.start(parties)
```

The files carry a version number. A file from a newer Konfetti than yours tells you to update instead of playing something wrong.

More examples are in the [samples](../samples).
