# Konfetti 🎊

[![platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16s)
[![License: ISC](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/ISC)

Let's start celebrate more together with our users by popping the confetti on their screens! 🎉 Create realistic confetti by implementing this lightweight particle system.

## Demo app

[<img src="media/konfetti_demo.gif" width="250" />]()

#### Sample app

Download on Google Play:

<a href="https://play.google.com/store/apps/details?id=nl.dionsegijn.confettiattempt">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or download the APK here [here](https://github.com/DanielMartinus/Konfetti/releases/download/1.0/sample_app.apk)

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
    .addSizes(Size.SMALL)
    .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
    .stream(300, 5000L)
```
<small><i>This example is written in Kotlin</i></small>

## Download
Just add the following dependency in your app's build.gradle


```groovy
dependencies {
      compile 'nl.dionsegijn:konfetti:1.0'
}
```

## Contribute

There is always room for improvement.

#### Report issue

Did you encounter bugs? Report them [here](https://github.com/DanielMartinus/Konfetti/issues). The more relevant information you provide the easier and faster it can be resolved.

#### Contribute

As mentioned, there is always room for improvement. Do you have any performance improvement ideas? Please suggest them [here](https://github.com/DanielMartinus/Konfetti/issues). Before submitting a large Pull Request, creating an issue to discuss your ideas would be the preferred way so we can be sure it is in line with other improvements currently being developed. Is it a simple improvement? Go ahead and submit a Pull Request! I very welcome any contributions.

## Roadmap

In line with the previous contribute section there are some already known issues that could be resolved and are open for discussion.

- Determining the size of the particles in the current implementation is not ideal. More here: [#7 Confetti size system](https://github.com/DanielMartinus/Konfetti/issues/7)
- A performance improvent to the library could for one be to implement a shared object poul amongst all particle systems instead of having them to handle confetti instances themselves.

## License 

Konfetti is released under the ISC license. See [LICENSE](https://github.com/DanielMartinus/Konfetti/blob/master/LICENSE) for details.
