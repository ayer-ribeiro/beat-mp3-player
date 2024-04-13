package dev.ayer.audioplayer.domain.usecases.songcompleted

import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository

internal class ShouldRepeatSongOnSongCompletedUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun shouldRepeatSong(): Boolean {
        return userPreferencesRepository.getRepeatMode() == RepeatMode.REPEAT_ONE
    }
}