package dev.ayer.audioplayer.domain

import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.entity.RepeatMode

internal class RepeatModeRequestManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    fun requestRepeatModeChange() {
        when(userPreferencesRepository.getRepeatMode()) {
            RepeatMode.NONE -> userPreferencesRepository.updateRepeatMode(RepeatMode.REPEAT_ALL)
            RepeatMode.REPEAT_ALL -> userPreferencesRepository.updateRepeatMode(RepeatMode.REPEAT_ONE)
            RepeatMode.REPEAT_ONE -> userPreferencesRepository.updateRepeatMode(RepeatMode.NONE)
        }
    }
}
