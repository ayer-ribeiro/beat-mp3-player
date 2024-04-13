package dev.ayer.medialibrary.domain.usecases

import dev.ayer.medialibrary.entity.media.LibrarySong

class SongsWithAlphabeticalOrderUseCase(songs: List<LibrarySong>) {
    val songsWithAlphabetical = songs.sortedBy { it.name }
}