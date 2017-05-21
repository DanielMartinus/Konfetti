package nl.dionsegijn.konfettidemo.configurations.settings

/**
 * Created by dionsegijn on 5/21/17.
 */
class TopBurstConfiguration : Configuration {

    override fun getColors(): IntArray {
        return intArrayOf(0xfce18a, 0xff726d, 0xb48def, 0xf4306d)
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
