package dev.ayer.audioplayer.domain.playerstate

import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.util.ListenerWrapper

interface PlayerState {
    val listeners: ListenerWrapper<PlayerStateListener>

    suspend fun getCurrentSongCurrentTime(): PlayerTime
    suspend fun getCurrentSongPercentage(): Percentage
    suspend fun getCurrentSongDuration(): PlayerTime
    suspend fun isPlaying(): Boolean
    suspend fun isLoading(): Boolean
    fun isShuffleEnabled(): Boolean
    fun getRepeatMode(): RepeatMode
}
