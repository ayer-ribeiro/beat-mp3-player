package dev.ayer.medialibrary.domain

import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.domain.repository.MediaLibraryRepository

//TODO: Apagar
internal class MediaLibraryUseCase(private val repository: MediaLibraryRepository) {

    private fun ArrayList<LibrarySong>.sortedByLastModified(): List<LibrarySong> {
        return this.sortedBy { it.lastModified }
    }

    private fun ArrayList<LibraryAlbum>.sortedByAlbumYear(): List<LibraryAlbum> {
        return this.sortedBy { it.releaseYear }
    }
}
