package dev.ayer.audioplayer.domain

import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository

internal class ShuffleChangeRequestManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    fun requestShuffleChange() {
        userPreferencesRepository.updateShuffleEnabled(
            !userPreferencesRepository.isShuffleEnabled()
        )
    }
}