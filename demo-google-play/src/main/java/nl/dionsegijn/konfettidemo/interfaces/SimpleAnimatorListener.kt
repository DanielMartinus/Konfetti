package nl.dionsegijn.konfettidemo.interfaces

import android.animation.Animator
import android.widget.SeekBar

/**
 * Created by dionsegijn on 5/24/17.
 */
open class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationStart(animation: Animator?) {}

}
