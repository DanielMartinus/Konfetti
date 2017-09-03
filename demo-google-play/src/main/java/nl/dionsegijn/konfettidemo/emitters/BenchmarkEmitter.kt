package nl.dionsegijn.konfettidemo.emitters

import android.os.SystemClock
import nl.dionsegijn.konfetti.emitters.Emitter

/**
 * Created by dionsegijn on 9/3/17.
 */
class BenchmarkEmitter : Emitter() {

    var cycle: Int = 0
    var amount: Int = 1
    override fun createConfetti(deltaTime: Float) {
        calculateFps()
        if(fps > 50) {
            if(cycle >= 60) {
                amount++
                cycle = 0
            }
        } else {
            if(cycle >= 60) {
                amount--
                cycle = 0
            }
        }
        if(amount < 1) amount = 1
        (1..amount).forEach { addConfettiFunc?.invoke() }
        cycle++
    }

    override fun isFinished(): Boolean = false

    var fps = 0
    var fpsCounter = 0
    var fpsTime: Long = 0
    fun calculateFps() {
        if (SystemClock.uptimeMillis() - fpsTime > 1000) {
            fpsTime = SystemClock.uptimeMillis()
            fps = fpsCounter
            fpsCounter = 0
        } else {
            fpsCounter++
        }
    }
}
