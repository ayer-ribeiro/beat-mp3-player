package dev.ayer.audioplayer.domain.repository

import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.domain.repository.listeners.UserPreferencesRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper

internal interface UserPreferencesRepository {
    val listeners: ListenerWrapper<UserPreferencesRepositoryListener>

    fun isShuffleEnabled(): Boolean
    fun getRepeatMode(): RepeatMode

    fun updateShuffleEnabled(isEnabled: Boolean)
    fun updateRepeatMode(repeatMode: RepeatMode)
}