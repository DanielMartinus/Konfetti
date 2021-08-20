package nl.dionsegijn.konfettidemo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import nl.dionsegijn.konfetti_core.models.Shape
import nl.dionsegijn.konfetti_core.models.Size
import nl.dionsegijn.konfettidemo.configurations.settings.Configuration
import nl.dionsegijn.konfettidemo.configurations.settings.ConfigurationManager
import nl.dionsegijn.konfettidemo.databinding.ActivityMainBinding
import nl.dionsegijn.konfettidemo.interfaces.OnConfigurationChangedListener
import nl.dionsegijn.konfettidemo.interfaces.OnSimpleTabSelectedListener

class MainActivity : AppCompatActivity(), OnConfigurationChangedListener {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val updateInfoHandler = Handler()
    private lateinit var updateInfoRunnable: Runnable

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabSelectionBottomSheetBehavior()
        binding.viewConfigurationControls.onConfigurationChanged = this
        bottomSheetBehavior = BottomSheetBehavior.from(binding.viewConfigurationControls)
        velocityTest()
        binding.viewKonfetti.setOnClickListener {
            startConfetti()
        }

        updateInfoRunnable = Runnable {
            updateSystemsInfo(); updateInfoHandler.postDelayed(updateInfoRunnable, 50)
        }
        updateInfoHandler.post(updateInfoRunnable)
    }

    /**
     * Implement expand and collapse behavior for the BottomSheet used to display the configuration
     * options.
     * - Reselect a tab and the bottom sheet will either collapse or expand depending on its current
     * state.
     * - Select a tab that wasn't active yet and the BottomSheet will expand
     */
    private fun setupTabSelectionBottomSheetBehavior() {
        binding.viewConfigurationControls.setOnTabSelectedListener(object :
            OnSimpleTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val state: Int =
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        BottomSheetBehavior.STATE_EXPANDED
                    } else {
                        BottomSheetBehavior.STATE_COLLAPSED
                    }
                bottomSheetBehavior.state = state
            }
        })
    }

    private fun startConfetti() {
        val config = binding.viewConfigurationControls.configuration.active
        val selectedColors = config.colors.map { color(it) }.toIntArray()
        when (config.type) {
            Configuration.TYPE_STREAM_FROM_TOP -> streamFromTop(config, selectedColors)
            Configuration.TYPE_BURST_FROM_CENTER -> burstFromCenter(config, selectedColors)
        }
    }

    private fun streamFromTop(config: Configuration, colors: IntArray) {
        if (!canIHaveMoreConfetti()) return
        binding.viewKonfetti.build()
            .addColors(*colors)
            .setDirection(0.0, 359.0)
            .setSpeed(config.minSpeed, config.maxSpeed)
            .setFadeOutEnabled(true)
            .setTimeToLive(config.timeToLive)
            .addShapes(*config.shapes)
            .addSizes(Size(12), Size(16, 6f))
            .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    private fun burstFromCenter(config: Configuration, colors: IntArray) {
        if (!canIHaveMoreConfetti()) return
        binding.viewKonfetti.build()
            .addColors(*colors)
            .setDirection(0.0, 359.0)
            .setSpeed(config.minSpeed, config.maxSpeed)
            .setFadeOutEnabled(true)
            .setTimeToLive(config.timeToLive)
            .addShapes(*config.shapes)
            .addSizes(Size(12), Size(16, 6f))
            .setPosition(
                binding.viewKonfetti.x + binding.viewKonfetti.width / 2,
                binding.viewKonfetti.y + binding.viewKonfetti.height / 3
            )
            .burst(100)
    }

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var speed: Int = 0
    private var degrees: Double = 0.0
    private fun velocityTest() {
        binding.viewKonfetti.setOnTouchListener { _, event ->
            val modeEnabled =
                binding.viewConfigurationControls.configuration.active.type == Configuration.TYPE_DRAG_AND_SHOOT
            when (event.action) {
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
                    if (speed > 10) speed = 0
                }
                MotionEvent.ACTION_UP -> {
                    if (!modeEnabled || !canIHaveMoreConfetti()) return@setOnTouchListener false
                    val colors =
                        binding.viewConfigurationControls.configuration.active.colors.map { color(it) }
                            .toIntArray()
                    binding.viewKonfetti.build()
                        .addColors(*colors)
                        .setDirection(degrees - 50, degrees + 50)
                        .setSpeed(0f, speed + 5f)
                        .addShapes(Shape.Square, Shape.Circle)
                        .addSizes(Size(12), Size(16, 6f))
                        .setPosition(startX, startY)
                        .setTimeToLive(10000)
                        .setFadeOutEnabled(true)
                        .burst(200)
                }
            }
            return@setOnTouchListener modeEnabled
        }
    }

    fun color(resId: Int): Int = ContextCompat.getColor(applicationContext, resId)

    override fun onConfigurationChanged(selected: Configuration) {
        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = 300
        valueAnimator.addUpdateListener { animator ->
            val alpha: Float = animator.animatedValue as Float
            binding.textViewInstructions.alpha = alpha
            binding.viewIllustration.alpha = alpha
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                binding.textViewInstructions.setText(selected.instructions)
                binding.viewIllustration.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        selected.vector
                    )
                )
                binding.textViewInstructions.animate().alpha(1f).duration = 300
                binding.viewIllustration.animate().alpha(1f).duration = 300
            }
        })
        valueAnimator.start()
    }

    private fun updateSystemsInfo() {
        val activeSystems = binding.viewKonfetti.getActiveSystems()
        val activeParticles = activeSystems.sumBy { it.activeParticles() }
        binding.viewSystemInfo.text =
            "Active systems: ${activeSystems.size} \nActive particles: $activeParticles"
    }

    /**
     * Check if more confetti is allowed right now.
     * The system has its limitations and as long as there is no shared object poul yet
     * there is no nice way of limiting the resources foreach particle system.
     */
    private fun canIHaveMoreConfetti(): Boolean {
        if (binding.viewConfigurationControls.configuration.maxParticleSystemsAlive
            == ConfigurationManager.PARTICLE_SYSTEMS_INFINITE
        ) {
            return true
        } else if (binding.viewKonfetti.getActiveSystems().size <= 6) {
            return true
        }
        return false
    }
}
