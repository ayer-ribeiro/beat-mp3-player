package dev.ayer.medialibrary.entity.media

import dev.ayer.medialibrary.entity.Duration
import dev.ayer.medialibrary.entity.keys.SongId
import java.util.*

class LibrarySong(
    val id: SongId,
    val name: String?,
    val lastModified: Date,
    val duration: Duration,
    val artist: LibraryArtist,
    val album: LibraryAlbum,
): Media<LibrarySong> {

    override fun hasSameId(other: LibrarySong): Boolean {
        return this.id == other.id
    }
}