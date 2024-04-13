package dev.ayer.audioplayer.domain

import dev.ayer.audioplayer.domain.repository.PlayerRepository

internal class TogglePlayPauseManager(
    private val playerRepository: PlayerRepository,
) {
    suspend fun togglePlayPause() {
        if (playerRepository.isPlaying()) {
           playerRepository.pause()
        } else {
            playerRepository.resume()
        }
    }
}
