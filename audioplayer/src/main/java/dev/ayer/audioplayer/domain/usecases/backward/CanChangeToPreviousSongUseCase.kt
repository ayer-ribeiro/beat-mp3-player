package dev.ayer.audioplayer.domain.usecases.backward

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository

internal class CanChangeToPreviousSongUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) {
    fun canChangeToPreviousSong(): Boolean {
        return currentPlaylistRepository.hasPreviousSong()
    }
}
