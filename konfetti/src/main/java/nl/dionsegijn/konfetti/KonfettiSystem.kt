package nl.dionsegijn.konfetti

import nl.dionsegijn.konfetti_core.ParticleSystem

class KonfettiSystem(private val konfettiView: KonfettiView): ParticleSystem() {

    /**
     * Add the system to KonfettiView. KonfettiView will initiate the rendering
     */
    override fun start() {
        konfettiView.start(this)
    }

    override fun stop() {
        konfettiView.stop(this)
    }
}
