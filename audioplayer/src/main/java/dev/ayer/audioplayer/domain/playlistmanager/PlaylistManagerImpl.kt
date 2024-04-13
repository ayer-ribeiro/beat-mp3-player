package dev.ayer.audioplayer.domain.playlistmanager

import androidx.lifecycle.Lifecycle
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlaylistRepositoryListener
import dev.ayer.audioplayer.domain.repository.listeners.UserPreferencesRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper

internal class PlaylistManagerImpl(
    lifecycle: Lifecycle,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
): PlaylistManager {
    override val listeners = ListenerWrapper<PlaylistManagerListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    init {
        currentPlaylistRepository.listeners.addListener(lifecycle, object : PlaylistRepositoryListener {
            override fun onCurrentSongChanged(song: PlayerSong, index: Int, shouldAutoPlay: Boolean) {
                listenerOwner.callListenersFunction {
                    it.onCurrentSongChanged(song, index)
                }
            }
            override fun onChanged(songs: List<PlayerSong>) {
                listenerOwner.callListenersFunction {
                    it.onChanged(songs)
                }
            }
            override fun onSongRemoved(song: PlayerSong, index: Int) {
                listenerOwner.callListenersFunction {
                    it.onSongRemoved(song, index)
                }
            }
            override fun onSongAdded(song: PlayerSong, index: Int) {
                listenerOwner.callListenersFunction {
                    it.onSongAdded(song, index)
                }
            }

            override fun onCurrentIndexChanged(index: Int) {
                listenerOwner.callListenersFunction {
                    it.onCurrentIndexChanged(index)
                }
            }
        })
    }

    override fun setupPlaylist(songs: List<PlayerSong>, index: Int, autoplay: Boolean) {
        currentPlaylistRepository.setup(songs, index, autoplay)
    }

    override fun addAsNextToQueue(songs: List<PlayerSong>) {
        currentPlaylistRepository.addSongsToNextIndex(songs)
    }

    override fun addToEndOfQueue(songs: List<PlayerSong>) {
        currentPlaylistRepository.addSongsToEnd(songs)
    }

    override fun removeSongAt(index: Int) {
        currentPlaylistRepository.removeSongAtIndex(index)
    }

    override fun getCurrentSong(): PlayerSong? {
        return currentPlaylistRepository.getCurrentSong()
    }

    override fun getCurrentIndex(): Int {
        return currentPlaylistRepository.getCurrentIndex()
    }

    override fun getCurrentPlaylist(): List<PlayerSong> {
        return currentPlaylistRepository.getSongs()
    }
}
