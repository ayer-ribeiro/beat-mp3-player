package dev.ayer.audioplayer.domain.playlistmanager

import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.util.ListenerWrapper

interface PlaylistManager {
    val listeners: ListenerWrapper<PlaylistManagerListener>
    fun setupPlaylist(songs: List<PlayerSong>, index: Int, autoplay: Boolean)

    fun addAsNextToQueue(songs: List<PlayerSong>)
    fun addToEndOfQueue(songs: List<PlayerSong>)
    fun removeSongAt(index: Int)

    fun getCurrentSong(): PlayerSong?
    fun getCurrentIndex(): Int
    fun getCurrentPlaylist(): List<PlayerSong>
}
