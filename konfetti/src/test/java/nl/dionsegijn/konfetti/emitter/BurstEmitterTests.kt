package nl.dionsegijn.konfetti.emitter

import nl.dionsegijn.konfetti.emitters.BurstEmitter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by dionsegijn on 9/4/17.
 */
class BurstEmitterTests {

    private lateinit var burstEmitter: BurstEmitter
    private lateinit var mockInvokeMethodClass: MockInvokeMethodClass

    @Before
    fun before() {
        mockInvokeMethodClass = Mockito.mock(MockInvokeMethodClass::class.java)
        burstEmitter = BurstEmitter()
        burstEmitter.addConfettiFunc = mockInvokeMethodClass::invokeMethod
    }

    @Test
    fun test_Burst_BuildEmitter() {
        val amount = 100
        // Setup BurstEmitter
        burstEmitter.build(amount)

        burstEmitter.createConfetti(1f)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(amount)).invokeMethod()

        // Amount should not have changed, burst emitter only
        // create the particles one time after calling createConfetti
        burstEmitter.createConfetti(1f)
        Mockito.verify(mockInvokeMethodClass, Mockito.times(amount)).invokeMethod()
    }
}
