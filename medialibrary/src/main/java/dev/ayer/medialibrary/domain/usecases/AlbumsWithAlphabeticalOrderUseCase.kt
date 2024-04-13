package dev.ayer.medialibrary.domain.usecases

import dev.ayer.medialibrary.entity.media.LibraryAlbum

class AlbumsWithAlphabeticalOrderUseCase(albums: List<LibraryAlbum>) {
    val albumsWithAlphabeticalOrder = albums.sortedBy { it.name }
}