package dev.ayer.dmusic.domain

import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong

interface LibraryRepository {
    fun getSongs(): List<LibrarySong>
    fun getAlbums(): List<LibraryAlbum>
    fun getArtist(): List<LibraryArtist>
}