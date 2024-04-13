package dev.ayer.audioplayer.domain.playerstate

import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.RepeatMode

interface PlayerStateListener {
    fun onCurrentTimeChangedMillis(timeMillis: Long, percentage: Percentage)
    fun onLoadingChanged(loading: Boolean)
    fun onSongPlaying()
    fun onSongPaused()
    fun onSongEnded()
    fun onSongError()
    fun onShuffleChanged(isShuffleEnabled: Boolean)
    fun onRepeatModeChanged(mode: RepeatMode)
}
