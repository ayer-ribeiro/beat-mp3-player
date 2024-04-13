package dev.ayer.audioplayer.domain.usecases.backward

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository

internal class ShouldChangeToLastPlaylistSongOnBackwardRequestUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
) {

    fun shouldChangeToLastSong(): Boolean {
        val currentIndex = currentPlaylistRepository.getCurrentIndex()
        val lastSong = currentPlaylistRepository.getLastSong()

        return currentIndex == 0 && lastSong != null
    }
}