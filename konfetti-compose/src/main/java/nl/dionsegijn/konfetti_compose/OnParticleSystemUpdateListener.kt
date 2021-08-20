package nl.dionsegijn.konfetti_compose

import nl.dionsegijn.konfetti_core.ParticleSystem

interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(system: ParticleSystem, activeSystems: Int)
    fun onParticleSystemEnded(system: ParticleSystem, activeSystems: Int)
}
