package nl.dionsegijn.konfetti_compose

import nl.dionsegijn.konfetti_core.PartySystem

interface OnParticleSystemUpdateListener {
    fun onParticleSystemEnded(system: PartySystem, activeSystems: Int)
}
