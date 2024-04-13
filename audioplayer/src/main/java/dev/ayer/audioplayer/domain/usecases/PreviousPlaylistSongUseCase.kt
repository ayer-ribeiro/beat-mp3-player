package dev.ayer.audioplayer.domain.usecases

import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlaylistShuffleRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.entity.media.PlayerSong

internal class PreviousPlaylistSongUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val playerShuffleRepository: PlaylistShuffleRepository,
) {
    fun previousSongOrNull(): PlayerSong? {
        if (!userPreferencesRepository.isShuffleEnabled()) {
            return currentPlaylistRepository.getPreviousSong()
        }

        val currentIndex = currentPlaylistRepository.getCurrentIndex()
        val hasPreviousSong = currentPlaylistRepository.hasPreviousSong()

        if (hasPreviousSong) {
            return playerShuffleRepository.getSongForIndex(currentIndex - 1)
        } else {
            return null
        }
    }
}