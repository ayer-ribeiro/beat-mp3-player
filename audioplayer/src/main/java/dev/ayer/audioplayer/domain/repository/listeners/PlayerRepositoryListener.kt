package dev.ayer.audioplayer.domain.repository.listeners

import dev.ayer.audioplayer.entity.Percentage

internal interface PlayerRepositoryListener {
    fun onCurrentTimeChangedMillis(timeMillis: Long, percentage: Percentage)
    fun onLoadingChanged(loading: Boolean)
    fun onSongPlaying()
    fun onSongPaused()
    fun onSongEnded()
    fun onSongError()
}
