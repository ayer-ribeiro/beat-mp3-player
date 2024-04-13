package dev.ayer.medialibrary.entity.media

import dev.ayer.medialibrary.entity.keys.ArtistId

class LibraryArtist(
    val id: ArtistId,
    val name: String?,
    private val _songs: ArrayList<LibrarySong>,
    private val _albums: ArrayList<LibraryAlbum>
): Media<LibraryArtist> {

    val songs: List<LibrarySong>
        get() = _songs

    val albums: List<LibraryAlbum>
        get() = _albums

    internal fun addSong(song: LibrarySong) {
        val alreadyHasSong = _songs.any { it.id == song.id }
        if (!alreadyHasSong) {
            _songs.add(song)
        }
    }

    internal fun addAlbum(album: LibraryAlbum) {
        val alreadyHasAlbum = _albums.any { it.id.androidId == album.id.androidId }
        if (!alreadyHasAlbum) {
            _albums.add(album)
        }
    }

    val albumsWithAlphabeticalOrder: List<LibraryAlbum>
        get() = _albums.sortedBy { it.name }
    val albumsWithReleaseYearOrder: List<LibraryAlbum>
        get() = _albums.sortedBy { it.releaseYear }
    val songsWithAlphabeticalOrder: List<LibrarySong>
        get() = _songs.sortedBy { it.name }

    override fun hasSameId(other: LibraryArtist): Boolean {
        return this.id.androidId == other.id.androidId
    }
}
