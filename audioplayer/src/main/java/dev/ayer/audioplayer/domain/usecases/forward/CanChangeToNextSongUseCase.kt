package dev.ayer.audioplayer.domain.usecases.forward

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository

internal class CanChangeToNextSongUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) {
    fun canChangeToNext(): Boolean {
        return currentPlaylistRepository.hasNextSong()
    }
}