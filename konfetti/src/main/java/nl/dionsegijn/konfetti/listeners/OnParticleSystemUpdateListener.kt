package nl.dionsegijn.konfetti.listeners

import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.ParticleSystem

/**
 * Created by dionsegijn on 5/31/17.
 */
interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(view: KonfettiView, system: ParticleSystem, activeSystems: Int)
    fun onParticleSystemEnded(view: KonfettiView, system: ParticleSystem, activeSystems: Int)
}
