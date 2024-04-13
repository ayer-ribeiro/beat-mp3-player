package dev.ayer.medialibrary.entity.keys

class ArtistId private constructor(val androidId: Long) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtistId
        if (androidId != other.androidId) return false

        return true
    }

    override fun hashCode(): Int {
        return androidId.hashCode()
    }

    companion object {
        fun fromAndroidId(androidId: Long): ArtistId {
            return ArtistId(androidId)
        }
    }
}
