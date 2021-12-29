package nl.dionsegijn.konfetti.compose

import nl.dionsegijn.konfetti.core.PartySystem

interface OnParticleSystemUpdateListener {
    fun onParticleSystemEnded(system: PartySystem, activeSystems: Int)
}
