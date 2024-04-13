package dev.ayer.audioplayer.domain.repository.listeners

import dev.ayer.audioplayer.entity.RepeatMode

internal interface UserPreferencesRepositoryListener {
    fun onShuffleChanged(isEnabled: Boolean)
    fun onRepeatModeChanged(mode: RepeatMode)
}
