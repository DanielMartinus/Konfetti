package nl.dionsegijn.konfetti.xml.listeners

import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.Party

/**
 * Created by dionsegijn on 5/31/17.
 */
interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(view: KonfettiView, party: Party, activeSystems: Int)
    fun onParticleSystemEnded(view: KonfettiView, party: Party, activeSystems: Int)
}
