package nl.dionsegijn.konfetti_compose

import nl.dionsegijn.konfetti.core.PartySystem

interface OnParticleSystemUpdateListener {
    fun onParticleSystemEnded(system: PartySystem, activeSystems: Int)
}
