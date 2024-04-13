package dev.ayer.audioplayer.data

import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlaylistRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper

internal class CurrentPlaylistRepositoryImpl: CurrentPlaylistRepository {
    private val songs = ArrayList<PlayerSong>()

    private var currentIndex: Int = 0

    override val listeners = ListenerWrapper<PlaylistRepositoryListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    override fun setup(
        songs: List<PlayerSong>,
        currentIndex: Int,
        shouldAutoPlay: Boolean
    ) {
        this.songs.clear()
        this.songs.addAll(songs)

        val clampedIndex = currentIndex.coerceAtLeast(0).coerceAtMost(songs.size - 1)
        this.currentIndex = clampedIndex

        listenerOwner.callListenersFunction {
            it.onChanged(songs)
            it.onCurrentSongChanged(songs[clampedIndex], clampedIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(clampedIndex)
        }
    }


    override fun getSongs(): List<PlayerSong> {
       return songs
    }

    override fun getCurrentIndex(): Int {
        return this.currentIndex
    }

    override fun getCurrentSong(): PlayerSong? {
        if (this.currentIndex >= this.songs.size) {
            return null
        }

        return this.songs[this.currentIndex]
    }

    override fun hasNextSong(): Boolean {
        return currentIndex < this.songs.size - 1
    }

    override fun hasPreviousSong(): Boolean {
        return currentIndex > 0
    }

    override fun getFirstSong(): PlayerSong? {
        return songs.firstOrNull()
    }

    override fun getLastSong(): PlayerSong? {
        return songs.lastOrNull()
    }

    override fun getNextSong(): PlayerSong? {
        return songs.getOrNull(currentIndex + 1)
    }

    override fun getPreviousSong(): PlayerSong? {
        return songs.getOrNull(currentIndex - 1)
    }

    override fun addSongsToNextIndex(songs: List<PlayerSong>) {
        val index = currentIndex + 1
        songs.reversed().forEach { song ->
            this.songs.add(index, song)
        }

        listenerOwner.callListenersFunction {
            it.onChanged(this.songs)
        }
    }

    override fun addSongsToEnd(songs: List<PlayerSong>) {
        songs.forEach { song ->
            this.songs.add(this.songs.size, song)
        }

        listenerOwner.callListenersFunction {
            it.onChanged(this.songs)
        }
    }

    override fun removeSongAtIndex(index: Int) {
        val shouldChangeToNextSong = currentIndex == index

        val songAtIndex = songs[index]
        songs.removeAt(index)
        listenerOwner.callListenersFunction {
            it.onSongRemoved(songAtIndex, index)
            it.onChanged(songs)

            if (shouldChangeToNextSong) {
                it.onCurrentSongChanged(songs[index], index, true)
                it.onCurrentIndexChanged(index)
            }
        }
    }

    override fun changeToIndex(index: Int, shouldAutoPlay: Boolean) {
        val songs = this.songs
        val newIndex = index.coerceAtLeast(0).coerceAtMost(songs.size - 1)
        currentIndex = newIndex
        listenerOwner.callListenersFunction {
            it.onCurrentSongChanged(songs[newIndex], newIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(newIndex)
        }
    }

    override fun changeToNext(shouldAutoPlay: Boolean) {
        val songs = this.songs
        val newIndex = (currentIndex + 1).coerceAtLeast(0).coerceAtMost(songs.size - 1)
        currentIndex = newIndex
        listenerOwner.callListenersFunction {
            it.onCurrentSongChanged(songs[newIndex], newIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(newIndex)
        }
    }

    override fun changeToPrevious(shouldAutoPlay: Boolean) {
        val songs = this.songs
        val newIndex = (currentIndex - 1).coerceAtLeast(0).coerceAtMost(songs.size - 1)
        currentIndex = newIndex
        listenerOwner.callListenersFunction {
            it.onCurrentSongChanged(songs[newIndex], newIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(newIndex)
        }
    }

    override fun changeToFirstSong(shouldAutoPlay: Boolean) {
        val songs = this.songs
        val newIndex = 0
        currentIndex = newIndex
        listenerOwner.callListenersFunction {
            it.onCurrentSongChanged(songs[newIndex], newIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(newIndex)
        }
    }

    override fun changeToLastSong(shouldAutoPlay: Boolean) {
        val songs = this.songs
        val newIndex = songs.size - 1
        currentIndex = newIndex
        listenerOwner.callListenersFunction {
            it.onCurrentSongChanged(songs[newIndex], newIndex, shouldAutoPlay)
            it.onCurrentIndexChanged(newIndex)
        }
    }
}
