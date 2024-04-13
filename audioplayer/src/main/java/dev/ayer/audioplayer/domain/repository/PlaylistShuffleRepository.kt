package dev.ayer.audioplayer.domain.repository

import dev.ayer.audioplayer.entity.media.PlayerSong

interface PlaylistShuffleRepository {
    fun setupPlaylist(songs: List<PlayerSong>)
    fun setupShuffledIndex(indexes: List<Int>)
    fun getShuffledPlaylist(): List<PlayerSong>
    fun getSongForIndex(index: Int): PlayerSong
}
