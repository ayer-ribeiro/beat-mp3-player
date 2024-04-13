package dev.ayer.audioplayer.entity.media

import android.os.Parcelable
import dev.ayer.audioplayer.entity.AndroidId
import dev.ayer.audioplayer.entity.PlayerTime
import kotlinx.parcelize.Parcelize

@Parcelize
class PlayerSong(
    val id: AndroidId,
    val name: String?,
    val duration: PlayerTime,
    val album: PlayerAlbum?,
    val artist: PlayerArtist?,
) : Parcelable
