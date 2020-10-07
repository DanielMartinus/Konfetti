package nl.dionsegijn.konfetti.emitter

import nl.dionsegijn.konfetti.emitters.StreamEmitter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by dionsegijn on 4/2/17.
 */
class StreamEmitterTests {

    private lateinit var streamEmitter: StreamEmitter
    private lateinit var mockInvokeMethodClass: MockInvokeMethodClass

    @Before
    fun before() {
        mockInvokeMethodClass = Mockito.mock(MockInvokeMethodClass::class.java)
        streamEmitter = StreamEmitter()
        streamEmitter.addConfettiFunc = mockInvokeMethodClass::invokeMethod
    }

    @Test
    fun test_Stream_OneParticlePerSecond() {
        streamEmitter.build(
            particlesPerSecond = 1,
            emittingTime = 3000
        )

        // Test how many particles are created in three seconds
        // Expected: 3
        val threeSeconds = 3f
        streamEmitter.createConfetti(threeSeconds)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(3)).invokeMethod()

        // Test how many particles are created in one more second
        // Expected: 3
        // Still 3 because emittingTime = 3000ms so the streamer is done emitting
        val oneSecond = 1f
        streamEmitter.createConfetti(oneSecond)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(3)).invokeMethod()
    }

    @Test
    fun test_Stream_HundredParticlesPerSecond() {
        streamEmitter.build(
            particlesPerSecond = 100,
            emittingTime = 3000
        )

        // Test how many particles are created in 100ms
        // Expected: 10
        streamEmitter.createConfetti(0.1f)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(10)).invokeMethod()

        // Test how many particles are created in one second
        // Expected: 110 (including 10 from the previous test)
        val oneSecond = 1f
        streamEmitter.createConfetti(oneSecond)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(110)).invokeMethod()
    }

    @Test
    fun test_Stream_tenParticlesPerSecond_maxTwelveParticles() {
        streamEmitter.build(
            particlesPerSecond = 10,
            maxParticles = 12
        )

        // Test how many particles are created in one second
        // Expected: 10
        streamEmitter.createConfetti(1f)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(10)).invokeMethod()

        // Another second means 2 more particles instead of 10 because maximum is reached
        streamEmitter.createConfetti(1f)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(12)).invokeMethod()
    }
}

class MockInvokeMethodClass {
    fun invokeMethod() {
    }
}
