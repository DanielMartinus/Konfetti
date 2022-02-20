<!-- LOGO -->
<br />
<h1>
<p align="center">
  <img src="https://user-images.githubusercontent.com/1636897/147644327-112cc446-75ea-4477-80ac-1d0cd60fc45e.png" alt="Logo">
</h1>
<p align="center">
    Easily celebrate little and big moments in your app with this lightweight confetti library!
    <br />
</p>

<p align="center">
    <a href="https://opensource.org/licenses/ISC"><img alt="License" src="https://img.shields.io/badge/License-ISC-yellow.svg"/></a>
    <a href="https://android-arsenal.com/api?level=16s"><img alt="API level 16" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>
    <a href="http://twitter.com/dionsegijn"><img alt="API level 16" src="https://img.shields.io/badge/Twitter-@dionsegijn-blue.svg?style=flat"/></a>
    <a href="https://github.com/DanielMartinus/Konfetti/actions"><img alt="Build Status" src="https://github.com/DanielMartinus/Konfetti/workflows/CI/badge.svg"/></a>
</p>

<p align="center">
  <a href="#getting-started">Getting started</a> •
  <a href="#usage">How To Use</a> •
  <a href="#community">Community</a> •
  <a href="#contribute">Contribute</a> •
  <a href="#report-an-issue">Report issue</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  <strong>New version: 2.0.0 is now released! Jetpack compose support - improved animations and API - <a href="https://dionsegijn.dev/konfetti-migration-guide-v2.x.x">see migration guide here</a></strong>
</p>

## Getting started

Compose project:
```groovy
dependencies {
    implementation 'nl.dionsegijn:konfetti-compose:2.0.2'
}
```

View based (XML) project:
```groovy
dependencies {
    implementation 'nl.dionsegijn:konfetti-xml:2.0.2'
}
```

Find latest version and release notes [here](https://github.com/DanielMartinus/Konfetti/releases)

## Usage

<p align="center">
  <strong>Samples:</strong></br>
  <a href="/samples/compose-kotlin/src/main">compose</a> •
  <a href="/samples/xml-kotlin/src/main">xml-kotlin</a> •
  <a href="/samples/xml-java/src/main">xml-java</a> •
  <a href="/samples/shared/src/main/java/nl/dionsegijn/samples/shared/Presets.kt">presets</a>
</p>  

<p align="center">
  <img width=300 src="https://user-images.githubusercontent.com/1636897/147699597-2d177073-a2f8-4f49-ad7e-c390dd374557.gif"/>
</p>

Configure your confetti using the Party configuration object. This holds all the information on how the confetti will be generated.
Almost all properties of a Party object have a default configuration! This makes it super easy to create beautiful and natural looking confetti.


The bare minimum you need is an **Emitter** to tell how often and how many confetti should spawn, like this:
```kotlin
Party(
    emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30)
)
```

**But the possibilities are endless!** You can fully control how the confetti will be generated and behave by customizing the values of the Party object.
An example of a customized Party configuration is this:

```kotlin
Party(
    speed = 0f,
    maxSpeed = 30f,
    damping = 0.9f,
    spread = 360,
    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
    position = Position.Relative(0.5, 0.3),
    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
)
```
_To learn more, see more samples linked at the top of [this section](#usage)_

### Party options

- `Angle` - **Int (default: 0)**: The direction the confetti will shoot. Use any integer between `0-360` or use presets like: Angle.TOP, Angle.RIGHT, Angle.BOTTOM, Angle.LEFT
- `spread` - **Int (default: 360)**: How wide the confetti will shoot in the direction of Angle. Use any integer between `0-360`. Use 1 to shoot in a straight line and 360 to form a circle
- `speed` - **Float (default: 30f)**: The start speed of the confetti at the time of creation.
- `maxSpeed` - **Float (default: 0f)**: Set to -1 to disable maxSpeed. A random speed between `speed` and `maxSpeed` will be chosen. Using randomness creates a more natural and realistic look to the confetti when animating.)
- `damping` - **Float (default: 0.9f)**: The rate at which the speed will decrease right after shooting the confetti
- `size` - **`List<Size>` (default: SMALL, MEDIUM, LARGE)**: The size of the confetti. Use: Size.SMALL, MEDIUM or LARGE for default sizes or
 create your custom size using a new instance of `Size`.
- `colors` - **`List<Int>` (default: 0xfce18a, 0xff726d, 0xf4306d, 0xb48def)**: List of colors that will be randomly picked from
- `shapes` - **`List<Shape>` (default: Shape.Square, Shape.Circle)**: Or use a custom shape with `Shape.DrawableShape`
- `timeToLive` - **Long (default: 2000)**: The time in milliseconds a particle will stay alive after that the confetti will disappear.
- `fadeOutEnabled` - **Boolean (default: true)**: If true and a confetti disappears because it ran out of time (set with timeToLive) it will slowly fade out. If set to falls it will instantly disappear from the screen.
- `position` - **Position (default: Position.Relative(0.5, 0.5))**: the location where the confetti will spawn from relative to the canvas. Use absolute
 coordinates with `[Position.Absolute]` or relative coordinates between 0.0 and 1.0 using `[Position.Relative]`. Spawn confetti between random locations using `[Position.between]`.
 - `delay` - **Int (default: 0)**: the amount of milliseconds to wait before the rendering of the confetti starts
 - `rotation` - **Rotation (default: Rotation)**: enable the 3D rotation of a Confetti. See [Rotation] class for the configuration
 options. Easily enable or disable it using [Rotation].enabled() or [Rotation].disabled() and control the speed of rotations.
 - `emitter` - **EmitterConfig**: Instructions how many and how often a confetti particle should spawn. Use Emitter(duration, timeUnit).max(amount) or Emitter(duration, timeUnit).perSecond(amount) to configure the Emitter. 

See Party implementation [here](/konfetti/core/src/main/java/nl/dionsegijn/konfetti/core/Party.kt)

### KonfettiView

Create a `KonfettiView` in your compose UI or add one to your xml layout depending on your setup.

Compose
```Kotlin
KonfettiView(
    modifier = Modifier.fillMaxSize(),
    parties = state.party,
)
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
    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
    position = Position.Relative(0.5, 0.3)
)
viewKonfetti.start(party)
```

And that's it! There are endless possibilities to configure the confetti. If you want to learn more on how to implement Konfetti in a java, xml or compose project then see the samples linked at the top of [this section](#usage)

## Community

- Follow me on twitter for updates [here](https://twitter.com/dionsegijn)
- Do you have any questions or need help implementing this library? Search if your question is already asked [here](https://github.com/DanielMartinus/Konfetti/issues?q=is%3Aissue)
- Or join our telegram chat group and maybe someone can help you out [here](https://t.me/konfetti_chat) 

## Contribute

Do you see any improvements or want to implement a missing feature? Contributions are very welcome!
- Is your contribution relatively small? You can, make your changes, run the code checks, open a PR and make sure the CI is green. That's it! 
- Are the changes big and do they make a lot of impact? Please open an issue [here](https://github.com/DanielMartinus/Konfetti/issues?q=is%3Aissue) or reach out and let's discuss.

Take into account that changes and requests can be rejected if they don't align with the **purpose of the library**. To not waste any time you can always open an issue [here](https://github.com/DanielMartinus/Konfetti/issues?q=is%3Aissue) to talk before you start making any changes.

### What is the purpose of this library?
To have a lightweight particle system to easily generate confetti particles to celebrate little and big moments. Even though this is a particle system the purpose is not to be a fully fledged particle system. Changes and features are meant to be in line with being a confetti library. A great example is the implementation of custom shapes by @mattprecious [here](https://github.com/DanielMartinus/Konfetti/pull/129).

## Report an issue

- Did you find an issue and want to fix it yourself? See [Contribute](#contribute) for more information
- Want to report an issue? You can do that [here](https://github.com/DanielMartinus/Konfetti/issues?q=is%3Aissue). By adding as much details when reporting the issue and steps to reproduce you improve the change it will be solved quickly. 

## License

Konfetti is released under the ISC license. See [LICENSE](https://github.com/DanielMartinus/Konfetti/blob/main/LICENSE) for details.
