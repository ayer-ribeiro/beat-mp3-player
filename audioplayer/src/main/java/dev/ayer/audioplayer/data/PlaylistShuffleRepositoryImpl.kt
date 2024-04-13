package dev.ayer.audioplayer.data

import dev.ayer.audioplayer.domain.repository.PlaylistShuffleRepository
import dev.ayer.audioplayer.entity.media.PlayerSong

class PlaylistShuffleRepositoryImpl: PlaylistShuffleRepository {
    private val songs = ArrayList<PlayerSong>()
    private val indexes = ArrayList<Int>()
    private val shuffledPlaylist = ArrayList<PlayerSong>()

    override fun setupPlaylist(songs: List<PlayerSong>) {
        this.songs.clear()
        this.songs.addAll(songs)
        updateShuffledPlaylist()
    }

    override fun setupShuffledIndex(indexes: List<Int>) {
        this.indexes.clear()
        this.indexes.addAll(indexes)
        updateShuffledPlaylist()
    }

    private fun updateShuffledPlaylist() {
        shuffledPlaylist.clear()
        indexes.forEach { index ->
            val songToAdd = this.songs[index]
            shuffledPlaylist.add(songToAdd)
        }
    }

    override fun getShuffledPlaylist(): List<PlayerSong> {
        return shuffledPlaylist
    }

    override fun getSongForIndex(index: Int): PlayerSong {
        return songs[index]
    }
}
