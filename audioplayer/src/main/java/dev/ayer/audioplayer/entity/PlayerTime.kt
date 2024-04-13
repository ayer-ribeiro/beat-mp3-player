package dev.ayer.audioplayer.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlayerTime private constructor(val timeMillis: Long) : Parcelable {
    companion object {
        fun fromMilliSeconds(timeMillis: Long): PlayerTime {
            return PlayerTime(timeMillis)
        }

        fun fromSeconds(timeSeconds: Double): PlayerTime {
            return PlayerTime((timeSeconds * 1000).toLong())
        }
    }
}
