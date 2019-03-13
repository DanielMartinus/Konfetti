import nl.dionsegijn.konfetti.emitters.Emitter

class InfiniteEmitter : Emitter() {
    override fun createConfetti(deltaTime: Float) {
        addConfettiFunc?.invoke()
    }

    override fun isFinished() = false
}