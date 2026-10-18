package nl.dionsegijn.konfetti.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.xml.image.ImageStore

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

    // Store for drawable images
    internal val imageStore = ImageStore()

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
     */
    fun burst(
        at: Offset,
        party: Party,
    ) = start(party.copy(position = Position.Absolute(at.x, at.y)))

    /**
     * Stop [party] right away. Its confetti disappears.
     */
    fun stop(party: Party) {
        pending.remove(party)
        // Running parties have their drawables swapped for references, compare against the same
        val stored = storeImages(party, imageStore)
        systems.removeAll { it.party == stored }
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
    }

    // Called by the view on each frame to start the parties that came in since the last one
    internal fun startPending(pixelDensity: Float) {
        if (pending.isEmpty()) return
        pending.forEach { systems.add(PartySystem(party = storeImages(it, imageStore), pixelDensity = pixelDensity)) }
        pending.clear()
    }

    internal fun isRunning() = pending.isNotEmpty() || systems.isNotEmpty()
}

/**
 * Creates a [KonfettiState] that survives recomposition. Pass it to [KonfettiView].
 */
@Composable
fun rememberKonfettiState(): KonfettiState = remember { KonfettiState() }
