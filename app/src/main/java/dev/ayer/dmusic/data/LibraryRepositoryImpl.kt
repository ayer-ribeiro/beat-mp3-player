package dev.ayer.dmusic.data

import android.content.Context
import dev.ayer.dmusic.domain.LibraryRepository
import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.transport.Library

class LibraryRepositoryImpl(context: Context): LibraryRepository {
    private val library = Library(context)

    override fun getSongs(): List<LibrarySong> {
        return library.getSongsWithAlphabeticalOrder()
    }

    override fun getAlbums(): List<LibraryAlbum> {
        return library.getAlbumsWithAlphabeticalOrder()
    }

    override fun getArtist(): List<LibraryArtist> {
        return library.getArtistsWithAlphabeticalOrder()
    }
}
