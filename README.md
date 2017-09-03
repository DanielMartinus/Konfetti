# Konfetti 🎊

[![platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16s)
[![License: ISC](https://img.shields.io/badge/License-ISC-yellow.svg)](https://opensource.org/licenses/ISC) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![Twitter](https://img.shields.io/badge/Twitter-@dionsegijn-blue.svg?style=flat)](http://twitter.com/dionsegijn)


Celebrate more with this lightweight confetti particle system 🎊 Create realistic confetti by implementing this easy to use library.

## Demo app

[<img src="media/konfetti_demo.gif" width="250" />]()

#### Sample app

Download on Google Play:

<a href="https://play.google.com/store/apps/details?id=nl.dionsegijn.confettiattempt">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or download the APK [here](https://github.com/DanielMartinus/Konfetti/releases/download/1.0/sample_app.apk)

## Usage


### XML

All you need in your layout is the KonfettiView to render the particles on:

```XML
<nl.dionsegijn.konfetti.KonfettiView
        android:id="@+id/viewKonfetti"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

### Example

```Kotlin
viewKonfetti.build()
    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
    .setDirection(0.0, 359.0)
    .setSpeed(1f, 5f)
    .setFadeOutEnabled(true)
    .setTimeToLive(2000L)
    .addShapes(Shape.RECT, Shape.CIRCLE)
    .addSizes(Size(12))
    .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
    .stream(300, 5000L)
```

## Download
Just add the following dependency in your app's build.gradle


```groovy
dependencies {
      compile 'nl.dionsegijn:konfetti:1.1.0'
}
```
 [ ![Download](https://api.bintray.com/packages/danielmartinus/maven/Konfetti/images/download.svg) ](https://bintray.com/danielmartinus/maven/Konfetti/_latestVersion)

## Contribute

There is always room for improvement.

#### Report issue

Did you encounter bugs? Report them [here](https://github.com/DanielMartinus/Konfetti/issues). The more relevant information you provide the easier and faster it can be resolved.

#### Contribute

As mentioned, there is always room for improvement. Do you have any performance improvement ideas? Please suggest them [here](https://github.com/DanielMartinus/Konfetti/issues). Before submitting a large Pull Request, creating an issue to discuss your ideas would be the preferred way so we can be sure it is in line with other improvements currently being developed. Is it a simple improvement? Go ahead and submit a Pull Request! I very welcome any contributions.

## Roadmap

In line with the previous contribute section there are some already known issues that could be resolved and are open for discussion.

- ~~Determining the size of the particles in the current implementation is not ideal. More here: [#7 Confetti size system](https://github.com/DanielMartinus/Konfetti/issues/7)~~
- A performance improvement to the library could for one be to implement a shared object pool amongst all particle systems instead of having them to handle confetti instances themselves.

## License 

Konfetti is released under the ISC license. See [LICENSE](https://github.com/DanielMartinus/Konfetti/blob/master/LICENSE) for details.
