package nl.dionsegijn.konfetti.models

import android.graphics.Bitmap

/**
 * Created by dionsegijn on 3/26/17.
 * Available shapes
 * RECT (rectangle)
 * Circle
 */
sealed class Shape {

    object RECT : Shape()
    object CIRCLE : Shape()
    class BITMAP(var bitmap: Bitmap,
        val colors: List<Int>,
        val scale: Float,
        val scaleRange: Float) : Shape()

}
