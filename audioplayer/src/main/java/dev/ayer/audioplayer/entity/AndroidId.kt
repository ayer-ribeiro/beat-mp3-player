package dev.ayer.audioplayer.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AndroidId private constructor(val androidId: Long) : Parcelable {
    companion object {
        fun fromAndroidId(androidId: Long): AndroidId {
            return AndroidId(androidId)
        }
    }
}
