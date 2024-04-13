package dev.ayer.medialibrary.entity.media

import dev.ayer.medialibrary.entity.keys.AlbumId

class LibraryAlbum(
    val id: AlbumId,
    val name: String,
    val artist: LibraryArtist,
    val releaseYear: Long,
    private val _songs: ArrayList<LibrarySong>,
): Media<LibraryAlbum> {

    val songs: List<LibrarySong>
        get() = _songs

    internal fun addSong(song: LibrarySong) {
        val alreadyHasSong = _songs.any { it.id == song.id }
        if (!alreadyHasSong) {
            _songs.add(song)
        }
    }

    override fun hasSameId(other: LibraryAlbum): Boolean {
        return this.id.androidId == other.id.androidId
    }
}
