package dev.ayer.audioplayer.domain.usecases.forward

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository

internal class ShouldChangeToFirstPlaylistSongOnForwardRequestUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
) {

    fun shouldChangeToFirstSong(): Boolean {
        val currentIndex = currentPlaylistRepository.getCurrentIndex()
        val currentPlaylistLastIndex = currentPlaylistRepository.getSongs().size - 1
        val firstSong = currentPlaylistRepository.getFirstSong()
        return currentIndex == currentPlaylistLastIndex && firstSong != null
    }
}