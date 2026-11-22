package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position

/**
 * Starts and stops the confetti of a [KonfettiView]. Create it with [rememberKonfettiState]
 * and call [start] or [burst] from anywhere, for example a click handler.
 * The view only asks for frames while a party is running.
 */
@Stable
class KonfettiState internal constructor() {
    // Parties waiting for the view to start them on its next frame
    internal val pending = mutableStateListOf<Party>()

    // Parties that are running
    internal val systems = mutableListOf<PartySystem>()

    // The original party passed to start, so stop() matches without storing drawables again
    private val originalParties = mutableMapOf<PartySystem, Party>()

    // Store for platform images like Android drawables, null when the platform has none
    internal var imageStore = createImageStore()
        private set

    /**
     * Start one or more parties. Parties that are already running keep going.
     */
    fun start(parties: List<Party>) {
        pending.addAll(parties)
    }

    /**
     * Start one or more parties. Parties that are already running keep going.
     */
    fun start(vararg parties: Party) = start(parties.toList())

    /**
     * Start [party] at a position in pixels, for example where the user tapped.
     * Returns the party that was started, pass it to [stop] to take the confetti back.
     */
    fun burst(
        at: Offset,
        party: Party,
    ): Party {
        val started = party.copy(position = Position.Absolute(at.x, at.y))
        start(started)
        return started
    }

    /**
     * Stop [party] right away. Its confetti disappears.
     */
    fun stop(party: Party) {
        pending.removeAll { it == party }
        systems.removeAll { system ->
            val matches = (originalParties[system] ?: system.party) == party
            if (matches) originalParties.remove(system)
            matches
        }
        pruneFinished()
    }

    /**
     * Stop creating new confetti and let the confetti that's already there finish.
     */
    fun stopGracefully() {
        pending.clear()
        systems.forEach { it.enabled = false }
    }

    /**
     * Stop every party right away. All confetti disappears.
     */
    fun reset() {
        pending.clear()
        systems.clear()
        originalParties.clear()
        imageStore = createImageStore()
    }

    // Called by the view on each frame to start the parties that came in since the last one
    internal fun startPending(pixelDensity: Float) {
        if (pending.isEmpty()) return
        pending.forEach { party ->
            val system = PartySystem(party = storePlatformImages(party, imageStore), pixelDensity = pixelDensity)
            systems.add(system)
            originalParties[system] = party
        }
        pending.clear()
    }

    // Releases stored drawables once no parties remain and trims finished systems from the map
    internal fun pruneFinished() {
        if (!isRunning()) {
            originalParties.clear()
            imageStore = createImageStore()
        } else if (originalParties.size > systems.size) {
            originalParties.keys.retainAll(systems.toSet())
        }
    }

    internal fun isRunning() = pending.isNotEmpty() || systems.isNotEmpty()
}

/**
 * Creates a [KonfettiState] that survives recomposition. Pass it to [KonfettiView].
 */
@Composable
fun rememberKonfettiState(): KonfettiState = remember { KonfettiState() }
