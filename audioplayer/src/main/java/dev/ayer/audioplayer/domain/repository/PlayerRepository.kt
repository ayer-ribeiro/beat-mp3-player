package dev.ayer.audioplayer.domain.repository

import dev.ayer.audioplayer.domain.repository.listeners.PlayerRepositoryListener
import dev.ayer.audioplayer.entity.AndroidId
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.util.ListenerWrapper

internal interface PlayerRepository {
    val listeners: ListenerWrapper<PlayerRepositoryListener>

    suspend fun resume()
    suspend fun pause()
    suspend fun loadSong(androidId: AndroidId, autoPlay: Boolean)
    suspend fun seekTo(time: PlayerTime)
    suspend fun seekTo(percentage: Percentage)

    suspend fun isPlaying(): Boolean
    suspend fun isLoading(): Boolean
    suspend fun isIdle(): Boolean
    suspend fun getCurrentTime(): PlayerTime
    suspend fun getSongDuration(): PlayerTime
    suspend fun getCurrentPercentage(): Percentage
}
