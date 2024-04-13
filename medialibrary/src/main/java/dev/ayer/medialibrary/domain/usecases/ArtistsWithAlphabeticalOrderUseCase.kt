package dev.ayer.medialibrary.domain.usecases

import dev.ayer.medialibrary.entity.media.LibraryArtist

class ArtistsWithAlphabeticalOrderUseCase(artists: List<LibraryArtist>) {
    val artistsWithAlphabeticalOrder = artists.sortedBy { it.name }
}