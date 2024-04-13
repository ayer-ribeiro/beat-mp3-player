package dev.ayer.medialibrary.domain.repository

import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.keys.SongId
import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong

internal interface MediaLibraryRepository {
    fun getSongs(): List<LibrarySong>
    fun getArtists(): List<LibraryArtist>
    fun getAlbums(): List<LibraryAlbum>
    fun getSongsHashMap(): HashMap<SongId, LibrarySong>
    fun getArtistsHashMap(): HashMap<ArtistId, LibraryArtist>
    fun getAlbumsHashMap(): HashMap<AlbumId, LibraryAlbum>
    fun refresh()
}
