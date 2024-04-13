package dev.ayer.medialibrary.data

import android.net.Uri

class AndroidMediaSongData(
    val androidId: Long,
    val uri: Uri,
    val title: String?,
    val artistId: Long,
    val artistName: String?,
    val albumId: Long,
    val albumName: String,
    val track: Int?,
    val dateModified: Long,
    val durationMs: Long,
    val year: Long,
)