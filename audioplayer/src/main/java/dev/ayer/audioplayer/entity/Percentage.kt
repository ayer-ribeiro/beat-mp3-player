package dev.ayer.audioplayer.entity

class Percentage private constructor(private val percentage: Double) {
    fun getWithMaxScale(maxScale: Double): Double {
        return percentage * maxScale
    }

    fun getWithMaxScale(maxScale: Int): Double {
        return percentage * maxScale
    }
    companion object {
        fun fromMaxScale(number: Double, max: Double): Percentage {
            return Percentage(number / max)
        }
        fun fromMaxScale(number: PlayerTime, max: PlayerTime): Percentage {
            return Percentage(number.timeMillis.toDouble() / max.timeMillis.toDouble())
        }

        fun fromMaxScale(number: Int, max: Int): Percentage {
            return Percentage((number.toDouble() / max.toDouble()))
        }

        fun fromMaxScale(number: Long, max: Long): Percentage {
            return Percentage((number.toDouble() / max.toDouble()))
        }
    }
}
