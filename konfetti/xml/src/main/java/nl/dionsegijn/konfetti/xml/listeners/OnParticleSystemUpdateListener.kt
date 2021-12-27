package nl.dionsegijn.konfetti.xml.listeners

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.xml.KonfettiView

/**
 * Created by dionsegijn on 5/31/17.
 */
interface OnParticleSystemUpdateListener {
    fun onParticleSystemStarted(view: KonfettiView, party: Party, activeSystems: Int)
    fun onParticleSystemEnded(view: KonfettiView, party: Party, activeSystems: Int)
}
