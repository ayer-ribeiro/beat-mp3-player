package dev.ayer.audioplayer.domain.usecases.backward

import dev.ayer.audioplayer.domain.repository.PlayerRepository

internal class ShouldRewindSongOnBackwardRequestUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend fun shouldRewind(): Boolean {
        return playerRepository.getCurrentTime().timeMillis > 3000L
    }
}