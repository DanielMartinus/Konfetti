package nl.dionsegijn.konfetti.models

import android.content.res.Resources

/**
 * Created by dionsegijn on 3/26/17.
 * [sizeInDp] the size of the confetti in dip
 * [mass] each size can have its own mass for slightly different behavior
 */
data class Size(val sizeInDp: Int, val mass: Float = 5f)

val Size.sizeDp: Int
    get() = (this.sizeInDp * Resources.getSystem().displayMetrics.density).toInt()
