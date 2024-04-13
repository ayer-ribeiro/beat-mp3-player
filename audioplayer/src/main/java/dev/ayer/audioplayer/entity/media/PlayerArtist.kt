package dev.ayer.audioplayer.entity.media

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlayerArtist(
    val androidId: Long,
    val artistName: String?
) : Parcelable