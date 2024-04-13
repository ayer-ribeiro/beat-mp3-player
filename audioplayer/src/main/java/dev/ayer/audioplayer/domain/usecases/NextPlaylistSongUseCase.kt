package dev.ayer.audioplayer.domain.usecases

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlaylistShuffleRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.entity.media.PlayerSong

internal class NextPlaylistSongUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val playerShuffleRepository: PlaylistShuffleRepository,
) {
    fun nextSongOrNull(): PlayerSong? {
        if (!userPreferencesRepository.isShuffleEnabled()) {
            return currentPlaylistRepository.getNextSong()
        }

        val currentIndex = currentPlaylistRepository.getCurrentIndex()
        val hasNextSong = currentPlaylistRepository.hasNextSong()

        if (hasNextSong) {
            return playerShuffleRepository.getSongForIndex(currentIndex + 1)
        } else {
            return null
        }
    }
}
