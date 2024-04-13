package dev.ayer.medialibrary.entity.keys

import android.net.Uri

class SongId private constructor(
    val androidId: Long,
    val uri: Uri
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongId
        if (androidId != other.androidId) return false

        return true
    }

    override fun hashCode(): Int {
        return androidId.hashCode()
    }

    companion object {
        fun createWithAndroidIdAndUri(
            androidId: Long,
            uri: Uri
        ): SongId {
            return SongId(androidId, uri)
        }
    }
}
