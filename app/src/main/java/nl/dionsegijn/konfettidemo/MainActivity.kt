package nl.dionsegijn.konfettidemo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleTabSelectedListener



/**
 * Created by dionsegijn on 3/25/17.
 */
class MainActivity : AppCompatActivity(), OnConfigurationChangedListener {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    val updateInfoHandler = Handler()
    lateinit var run: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupTabSelectionBottomSheetBehavior()
        viewConfigurationControls.setOnConfigurationChangedListener(this)
        bottomSheetBehavior = BottomSheetBehavior.from(viewConfigurationControls)
        velocityTest()
        viewKonfetti.setOnClickListener {
            startConfetti()
        }

        run = Runnable { updateSystemsInfo(); updateInfoHandler.postDelayed(run, 50) }
        updateInfoHandler.post(run)
    }

    /**
     * Implement expand and collapse behavior for the BottomSheet used to display the configuration
     * options.
     * - Reselect a tab and the bottom sheet will either collapse or expand depending on its current
     * state.
     * - Select a tab that wasn't active yet and the BottomSheet will expand
     */
    fun setupTabSelectionBottomSheetBehavior() {
        viewConfigurationControls.setOnTabSelectedListener(object : OnSimpleTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val state: Int
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
                bottomSheetBehavior.state = state
            }
        })
    }

    fun startConfetti() {
        val config = viewConfigurationControls.configuration.active
        val selectedColors = config.colors.map { color(it) }.toIntArray()
        when (config.type) {
            Configuration.TYPE_STREAM_FROM_TOP -> streamFromTop(config, selectedColors)
            Configuration.TYPE_BURST_FROM_CENTER -> burstFromCenter(config, selectedColors)
        }
    }

    fun streamFromTop(config: Configuration, colors: IntArray) {
        if(!canIHaveMoreConfetti()) return
        viewKonfetti.build()
                .addColors(*colors)
                .setDirection(0.0, 359.0)
                .setSpeed(config.minSpeed, config.maxSpeed)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(*config.shapes)
                .addSizes(Size(35f), Size(45f, 6f))
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                .stream(300, 5000L)
    }

    fun burstFromCenter(config: Configuration, colors: IntArray) {
        if(!canIHaveMoreConfetti()) return
        viewKonfetti.build()
                .addColors(*colors)
                .setDirection(0.0, 359.0)
                .setSpeed(config.minSpeed, config.maxSpeed)
                .setFadeOutEnabled(true)
                .setTimeToLive(config.timeToLive)
                .addShapes(*config.shapes)
                .addSizes(Size(35f), Size(45f, 6f))
                .setPosition(viewKonfetti.x + viewKonfetti.width / 2, viewKonfetti.y + viewKonfetti.height / 3)
                .burst(100)
    }

    var startX: Float = 0f
    var startY: Float = 0f
    var speed: Int = 0
    var degrees: Double = 0.0
    fun velocityTest() {
        viewKonfetti.setOnTouchListener { _, event ->
            val modeEnabled = viewConfigurationControls.configuration.active.type == Configuration.TYPE_DRAG_AND_SHOOT
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - startX
                    val dy = event.y - startY
                    val r = Math.atan2(dy.toDouble(), dx.toDouble()) // In radians
                    degrees = ((r * (180 / Math.PI) - 180) + 360) % 360

                    val length = Math.sqrt((dx * dx) + (dy * dy).toDouble())
                    speed = (length / 100).toInt()
                    if(speed > 10) speed = 0
                }
                MotionEvent.ACTION_UP -> {
                    if(!modeEnabled || !canIHaveMoreConfetti()) return@setOnTouchListener false
                    val colors = viewConfigurationControls.configuration.active.colors.map { color(it) }.toIntArray()
                    viewKonfetti.build()
                            .addColors(*colors)
                            .setDirection(degrees - 50, degrees + 50)
                            .setSpeed(0f, speed + 5f)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(Size(35f), Size(45f, 6f))
                            .setPosition(startX, startY)
                            .setTimeToLive(10000)
                            .setFadeOutEnabled(true)
                            .burst(200)
                }
            }
            return@setOnTouchListener modeEnabled
        }
    }

    fun color(resId: Int): Int {
        return ContextCompat.getColor(applicationContext, resId)
    }

    override fun onConfigurationChanged(selected: Configuration) {
        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = 300
        valueAnimator.addUpdateListener { animator ->
            val alpha: Float = animator.animatedValue as Float
            textViewInstructions.alpha = alpha
            viewIllustration.alpha = alpha
        }
        valueAnimator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                textViewInstructions.setText(selected.instructions)
                viewIllustration.setImageDrawable(ContextCompat.getDrawable(applicationContext, selected.vector))
                textViewInstructions.animate().alpha(1f).duration = 300
                viewIllustration.animate().alpha(1f).duration = 300
            }
        })
        valueAnimator.start()
    }

    fun updateSystemsInfo() {
        val activeSystems = viewKonfetti.systems.size
        val activeParticles = viewKonfetti.systems.sumBy { it.activeParticles() }
        viewSystemInfo.text = "Active systems: $activeSystems \nActive particles: $activeParticles"
    }

    /**
     * Check if more confetti is allowed right now.
     * The system has its limitations and as long as there is no shared object poul yet
     * there is no nice way of limiting the resources foreach particle system.
     */
    fun canIHaveMoreConfetti(): Boolean {
        if(viewConfigurationControls.configuration.maxParticleSystemsAlive
                == ConfigurationManager.PARTICLE_SYSTEMS_INFINITE) {
            return true
        } else if(viewKonfetti.systems.size <= 6) {
            return true
        }
        return false
    }

}
