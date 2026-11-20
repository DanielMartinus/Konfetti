# Migrating from 2.x to 3.0

For most Kotlin apps that use `Party` and `KonfettiView`, bumping the version is enough. Rebuild your app against 3.0 and keep every Konfetti artifact on 3.0, since libraries built against 2.x won't run with it. These are the things that changed:

- **Compose Multiplatform.** `konfetti-compose` is built with Compose Multiplatform 1.11. On Android that means min API 23 (was 21), compileSdk 35 or higher and Compose 1.11 or newer in your app.
- **Emitter takes a Duration.** Write `Emitter(5.seconds)` or `Emitter(5, DurationUnit.SECONDS)`. In Kotlin on Android and the JVM, `Emitter(5, TimeUnit.SECONDS)` still works. From Java, use `new Emitter(5L, DurationUnit.SECONDS)`. `Emitter.timeUnit` is now a `DurationUnit`.
- **Smoother on fast screens.** Confetti now moves by time instead of by frame. On 60 Hz screens it moves the same as before. On 90, 120 and 144 Hz screens it now looks the same as on 60 Hz, where before it fell, slowed down and faded at a different pace. Confetti now also fades exactly after `timeToLive`, a few frames sooner than before.
- **Parties are equal by value.** Two parties with the same settings are now equal, emitter included. `stop(party)` stops every running party equal to the one you pass.
- **Java reads presets directly.** Write `Size.SMALL` instead of `Size.Companion.getSMALL()`. `Rotation.enabled()` and `Drift.enabled()` work without `.Companion` too, the old way still works for those.
- **kotlin.random.** If you built your own renderer on `konfetti-core`, `PartySystem` and `PartyEmitter` now take a `kotlin.random.Random`. Wrap a `java.util.Random` with `asKotlinRandom()`.
- **Maven and Bazel.** `konfetti-core` is now a multiplatform library. Gradle picks the right artifact by itself. With Maven or Bazel, depend on `konfetti-core-jvm`.
- **iOS** supports `iosArm64` and `iosSimulatorArm64`, not the Intel simulator.

`konfetti-xml` and `konfetti-image` stay Android only and still support API 15.

Coming from 1.x? See the [2.0 migration guide](https://dionsegijn.dev/konfetti-migration-guide-v2.x.x) first.
