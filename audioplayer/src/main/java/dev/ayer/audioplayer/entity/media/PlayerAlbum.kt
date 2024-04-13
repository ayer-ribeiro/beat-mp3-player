package dev.ayer.audioplayer.entity.media

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlayerAlbum(
    val androidId: Long,
    val albumName: String,
) : Parcelable