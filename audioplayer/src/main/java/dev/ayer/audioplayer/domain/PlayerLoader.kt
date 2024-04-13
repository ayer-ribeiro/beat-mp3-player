package dev.ayer.audioplayer.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlayerRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlaylistRepositoryListener
import kotlinx.coroutines.launch

internal class PlayerLoader(
    private val lifecycle: Lifecycle,
    private val playerRepository: PlayerRepository,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
) {
    init {
        currentPlaylistRepository.listeners.addListener(lifecycle, object: PlaylistRepositoryListener {
            override fun onChanged(songs: List<PlayerSong>) {}
            override fun onSongRemoved(song: PlayerSong, index: Int) {}
            override fun onSongAdded(song: PlayerSong, index: Int) {}
            override fun onCurrentIndexChanged(index: Int) {}

            override fun onCurrentSongChanged(song: PlayerSong, index: Int, shouldAutoPlay: Boolean) {
                lifecycle.coroutineScope.launch {
                    playerRepository.loadSong(song.id, shouldAutoPlay)
                }
            }
        })
    }
}
