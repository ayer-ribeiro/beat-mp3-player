package dev.ayer.medialibrary.entity.keys

class AlbumId private constructor(val androidId: Long) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlbumId
        if (androidId != other.androidId) return false

        return true
    }

    override fun hashCode(): Int {
        return androidId.hashCode()
    }

    companion object {
        fun fromAndroidId(androidId: Long): AlbumId {
            return AlbumId(androidId)
        }
    }
}
