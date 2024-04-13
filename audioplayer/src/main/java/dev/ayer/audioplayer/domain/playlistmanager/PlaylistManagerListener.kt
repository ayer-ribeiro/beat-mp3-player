package dev.ayer.audioplayer.domain.playlistmanager

import dev.ayer.audioplayer.entity.media.PlayerSong

interface PlaylistManagerListener {
    fun onCurrentSongChanged(song: PlayerSong, index: Int)
    fun onCurrentIndexChanged(index: Int)
    fun onChanged(songs: List<PlayerSong>)
    fun onSongRemoved(song: PlayerSong, index: Int)
    fun onSongAdded(song: PlayerSong, index: Int)
}
