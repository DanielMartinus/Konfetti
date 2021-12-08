package nl.dionsegijn.konfetti.listeners

import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti_core.Party

/**
 * Created by dionsegijn on 5/31/17.
 */
interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(view: KonfettiView, party: Party, activeSystems: Int)
    fun onParticleSystemEnded(view: KonfettiView, party: Party, activeSystems: Int)
}
