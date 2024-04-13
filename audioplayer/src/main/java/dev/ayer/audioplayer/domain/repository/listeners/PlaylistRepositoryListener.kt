package dev.ayer.audioplayer.domain.repository.listeners

import dev.ayer.audioplayer.entity.media.PlayerSong

internal interface PlaylistRepositoryListener {
    fun onChanged(songs: List<PlayerSong>)
    fun onSongRemoved(song: PlayerSong, index: Int)
    fun onSongAdded(song: PlayerSong, index: Int)
    fun onCurrentIndexChanged(index: Int)
    fun onCurrentSongChanged(song: PlayerSong, index: Int, shouldAutoPlay: Boolean)
}