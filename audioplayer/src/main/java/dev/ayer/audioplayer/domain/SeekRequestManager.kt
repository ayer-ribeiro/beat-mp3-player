package dev.ayer.audioplayer.domain

import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlayerRepository

internal class SeekRequestManager(
    private val playerRepository: PlayerRepository
) {
    suspend fun seekTo(percentage: Percentage) {
        playerRepository.seekTo(percentage)
    }

    suspend fun seekTo(playerTime: PlayerTime) {
        playerRepository.seekTo(playerTime)
    }
}
