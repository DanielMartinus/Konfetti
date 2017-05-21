package nl.dionsegijn.konfettidemo.configurations.settings

import nl.dionsegijn.konfettidemo.R

/**
 * Created by dionsegijn on 5/21/17.
 */
class TopBurstConfiguration : Configuration {

    override fun getColors(): IntArray {
        return intArrayOf(R.color.yellow, R.color.orange, R.color.purple, R.color.pink)
    }

    override fun getMinDirection(): Double {
        return 0.0
    }

    override fun getMaxDirection(): Double {
        return 359.0
    }

    override fun getMinSpeed(): Float {
        return 1f
    }

    override fun getMaxSpeed(): Float {
        return 5f
    }

    override fun isFadeOutEnabled(): Boolean {
        return true
    }

    override fun getTimeToLive(): Long {
        return 2500L
    }

    override fun getShapes(): Array<nl.dionsegijn.konfetti.models.Shape> {
        return arrayOf(nl.dionsegijn.konfetti.models.Shape.RECT, nl.dionsegijn.konfetti.models.Shape.CIRCLE)
    }

    override fun getSizes(): Array<nl.dionsegijn.konfetti.models.Size> {
        return arrayOf(nl.dionsegijn.konfetti.models.Size.SMALL)
    }
}
