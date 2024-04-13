package dev.ayer.audioplayer.domain.usecases.songcompleted

import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository

internal class ShouldGoToNextSongOnSongCompletedUseCase(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun shouldGoToNextSong(): Boolean {
        return currentPlaylistRepository.hasNextSong()
                && userPreferencesRepository.getRepeatMode() != RepeatMode.REPEAT_ONE
    }
}