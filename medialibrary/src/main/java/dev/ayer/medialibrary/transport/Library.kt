package dev.ayer.medialibrary.transport

import android.content.Context
import dev.ayer.medialibrary.data.AndroidDatabaseFetcherImpl
import dev.ayer.medialibrary.data.AndroidMediaLibraryRepository
import dev.ayer.medialibrary.domain.LibraryCacheManager
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong

class Library(context: Context) {

    private val repository = AndroidMediaLibraryRepository(AndroidDatabaseFetcherImpl(context))
    private val libraryCacheManager = LibraryCacheManager(repository)

     fun getSongsWithAlphabeticalOrder(): List<LibrarySong> {
        return libraryCacheManager.getSongsWithAlphabeticalOrder()
    }

    fun getAlbumsWithAlphabeticalOrder(): List<LibraryAlbum> {
        return libraryCacheManager.getAlbumsWithAlphabeticalOrder()
    }

    fun getArtistsWithAlphabeticalOrder(): List<LibraryArtist> {
        return libraryCacheManager.getArtistsWithAlphabeticalOrder()
    }

    fun getArtistById(androidId: ArtistId): LibraryArtist? {
        return libraryCacheManager.getArtistsHashMap()[androidId]
    }

    fun getAlbumById(albumId: AlbumId): LibraryAlbum? {
        return libraryCacheManager.getAlbumsHashMap()[albumId]
    }
}
