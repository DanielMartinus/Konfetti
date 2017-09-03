package nl.dionsegijn.konfetti.emitters

/**
 * Created by dionsegijn on 9/03/17.
 *
 * An abstract class for creating a custom emitter
 * The only goal of the emitter is to tell when and how many particles to create
 */
abstract class Emitter {

    /**
     * Call this function to tell the RenderSystem to render a particle
     */
    var addConfettiFunc: (() -> Unit)? = null

    /**
     * This function is called on each update when the [RenderSystem] is active
     * Keep this function as light as possible otherwise you'll slow down the render system
     */
    abstract fun createConfetti(deltaTime: Float)

    /**
     * Tell the [RenderSystem] when the emitter is done creating particles
     * @return true if the renderSystem is not longer creating any particles
     *         false if the renderSystem is still busy
     */
    abstract fun isFinished(): Boolean
}
