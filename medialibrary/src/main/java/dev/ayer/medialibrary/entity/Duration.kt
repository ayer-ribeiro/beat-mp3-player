package dev.ayer.medialibrary.entity

class Duration private constructor (private val durationMs: Long) {

    fun toMinutes(): Float = durationMs.toFloat() / (1000 * 60).toFloat()
    fun toSeconds(): Long = durationMs / (1000)
    fun toMilliseconds(): Long = durationMs

    companion object {
        fun fromMinutes(durationMinutes: Long): Duration {
            return Duration(durationMinutes * 60 * 1000)
        }

        fun fromSeconds(durationSeconds: Long): Duration {
            return Duration(durationSeconds * 1000)
        }

        fun fromMilliseconds(durationMilliseconds: Long): Duration {
            return Duration(durationMilliseconds)
        }
    }
}
