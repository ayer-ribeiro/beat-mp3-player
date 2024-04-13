package dev.ayer.medialibrary.entity.keys

class SongPath private constructor(val filePath: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongPath
        if (filePath != other.filePath) return false

        return true
    }

    override fun hashCode(): Int {
        return filePath.hashCode()
    }

    companion object {
        fun fromFilePath(filePath: String): SongPath {
            return SongPath(filePath)
        }
    }
}
