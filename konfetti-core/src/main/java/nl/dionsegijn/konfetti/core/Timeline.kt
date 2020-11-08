package nl.dionsegijn.konfetti.core

class Timeline {

    val periods = mutableListOf<Period>()

    fun add(configuration: Configuration, startAt: Long = 0L): Timeline {
        val system = PartySystem(configuration)
        periods.add(Period(configuration, startAt))
        return this
    }

    data class Period(val configuration: Emitter2, val startAt: Long = 0L)
}

