package nl.dionsegijn.konfetti_compose

import nl.dionsegijn.konfetti_core.PartySystem

interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(system: PartySystem, activeSystems: Int)
    fun onParticleSystemEnded(system: PartySystem, activeSystems: Int)
}
