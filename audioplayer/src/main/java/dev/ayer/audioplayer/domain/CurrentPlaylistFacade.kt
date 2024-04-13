package dev.ayer.audioplayer.domain

import androidx.lifecycle.Lifecycle
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlaylistRepositoryListener
import dev.ayer.audioplayer.domain.repository.listeners.UserPreferencesRepositoryListener
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.util.ListenerWrapper

internal class CurrentPlaylistFacade(
    lifecycle: Lifecycle,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val currentShuffledPlaylistRepository: CurrentPlaylistRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): CurrentPlaylistRepository {

    private val shuffledPlaylistOriginalIndexes = ArrayList<Int>()

    override val listeners = ListenerWrapper<PlaylistRepositoryListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    private val isShuffleEnabled: Boolean
        get() = userPreferencesRepository.isShuffleEnabled()

    private val repositoryToUse: CurrentPlaylistRepository
        get() {
            return if (isShuffleEnabled) {
                currentShuffledPlaylistRepository
            } else {
                currentPlaylistRepository
            }
        }

    private val repositoryListener = object : PlaylistRepositoryListener {
        override fun onChanged(songs: List<PlayerSong>) {
            listenerOwner.callListenersFunction { it.onChanged(songs) }
        }

        override fun onSongRemoved(song: PlayerSong, index: Int) {
            listenerOwner.callListenersFunction { it.onSongRemoved(song, index) }
        }

        override fun onSongAdded(song: PlayerSong, index: Int) {
            listenerOwner.callListenersFunction { it.onSongAdded(song, index) }
        }

        override fun onCurrentIndexChanged(index: Int) {
            listenerOwner.callListenersFunction { it.onCurrentIndexChanged(index) }
        }

        override fun onCurrentSongChanged(song: PlayerSong, index: Int, shouldAutoPlay: Boolean) {
            listenerOwner.callListenersFunction { it.onCurrentSongChanged(song, index, shouldAutoPlay) }
        }
    }

    init {
        userPreferencesRepository.listeners.addListener(lifecycle, object : UserPreferencesRepositoryListener {
            override fun onRepeatModeChanged(mode: RepeatMode) {}
            override fun onShuffleChanged(isEnabled: Boolean) {
                if (isEnabled) {
                    currentShuffledPlaylistRepository.listeners.addListener(lifecycle, repositoryListener)
                    currentPlaylistRepository.listeners.removeListener(repositoryListener)
                } else {
                    currentPlaylistRepository.listeners.addListener(lifecycle, repositoryListener)
                    currentShuffledPlaylistRepository.listeners.removeListener(repositoryListener)
                }

                listenerOwner.callListenersFunction {
                    it.onChanged(repositoryToUse.getSongs())
                    it.onCurrentIndexChanged(repositoryToUse.getCurrentIndex())
                }
            }
        })

        repositoryToUse.listeners.addListener(lifecycle, repositoryListener)
    }

    override fun setup(songs: List<PlayerSong>, currentIndex: Int, shouldAutoPlay: Boolean) {
        shuffledPlaylistOriginalIndexes.clear()
        val shufflesSongs = ArrayList<PlayerSong>()
        val indexesSize = songs.size

        for (i in 0 until indexesSize) {
            shuffledPlaylistOriginalIndexes.add(i)
        }

        shuffledPlaylistOriginalIndexes.removeAt(currentIndex)
        shuffledPlaylistOriginalIndexes.shuffle()
        shuffledPlaylistOriginalIndexes.add(0, currentIndex)

        shuffledPlaylistOriginalIndexes.forEach { index ->
            shufflesSongs.add(songs[index])
        }

        currentPlaylistRepository.setup(songs, currentIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.setup(shufflesSongs, 0, shouldAutoPlay)
    }

    override fun getSongs(): List<PlayerSong> {
        return repositoryToUse.getSongs()
    }

    override fun getCurrentIndex(): Int {
        return repositoryToUse.getCurrentIndex()
    }

    override fun getCurrentSong(): PlayerSong? {
        return repositoryToUse.getCurrentSong()
    }

    override fun hasNextSong(): Boolean {
        return repositoryToUse.hasNextSong()
    }

    override fun hasPreviousSong(): Boolean {
        return repositoryToUse.hasPreviousSong()
    }

    override fun getFirstSong(): PlayerSong? {
        return repositoryToUse.getFirstSong()
    }

    override fun getLastSong(): PlayerSong? {
        return repositoryToUse.getLastSong()
    }

    override fun getNextSong(): PlayerSong? {
        return repositoryToUse.getNextSong()
    }

    override fun getPreviousSong(): PlayerSong? {
        return repositoryToUse.getPreviousSong()
    }

    override fun addSongsToNextIndex(songs: List<PlayerSong>) {
        currentPlaylistRepository.addSongsToNextIndex(songs)
        currentShuffledPlaylistRepository.addSongsToNextIndex(songs)
    }

    override fun addSongsToEnd(songs: List<PlayerSong>) {
        currentPlaylistRepository.addSongsToEnd(songs)
        currentShuffledPlaylistRepository.addSongsToEnd(songs)
    }

    override fun removeSongAtIndex(index: Int) {
        val shuffledPlaylistIndex = shuffledPlaylistOriginalIndexes.indexOf(index)

        currentPlaylistRepository.removeSongAtIndex(index)
        currentShuffledPlaylistRepository.removeSongAtIndex(shuffledPlaylistIndex)
    }

    override fun changeToIndex(index: Int, shouldAutoPlay: Boolean) {
        val originalNewIndex : Int
        val shuffledNewIndex : Int
        if (isShuffleEnabled) {
            shuffledNewIndex = index
            originalNewIndex = shuffledPlaylistOriginalIndexes[index]
        } else {
            originalNewIndex = index
            shuffledNewIndex = shuffledPlaylistOriginalIndexes.indexOf(index)
        }
        currentPlaylistRepository.changeToIndex(originalNewIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.changeToIndex(shuffledNewIndex, shouldAutoPlay)
    }

    override fun changeToNext(shouldAutoPlay: Boolean) {
        val originalNewIndex : Int
        val shuffledNewIndex : Int

        if (isShuffleEnabled) {
            shuffledNewIndex = (currentShuffledPlaylistRepository.getCurrentIndex() + 1)
                .coerceAtLeast(0)
                .coerceAtMost(currentShuffledPlaylistRepository.getSongs().size - 1)

            originalNewIndex = shuffledPlaylistOriginalIndexes[shuffledNewIndex]
        } else {
            originalNewIndex = (currentPlaylistRepository.getCurrentIndex() + 1)
                .coerceAtLeast(0)
                .coerceAtMost(currentPlaylistRepository.getSongs().size - 1)

            shuffledNewIndex = shuffledPlaylistOriginalIndexes.indexOf(originalNewIndex)
        }

        currentPlaylistRepository.changeToIndex(originalNewIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.changeToIndex(shuffledNewIndex, shouldAutoPlay)
    }

    override fun changeToPrevious(shouldAutoPlay: Boolean) {
        val originalNewIndex : Int
        val shuffledNewIndex : Int

        if (isShuffleEnabled) {
            shuffledNewIndex = (currentShuffledPlaylistRepository.getCurrentIndex() - 1)
                .coerceAtLeast(0)
                .coerceAtMost(currentShuffledPlaylistRepository.getSongs().size - 1)

            originalNewIndex = shuffledPlaylistOriginalIndexes[shuffledNewIndex]
        } else {
            originalNewIndex = (currentPlaylistRepository.getCurrentIndex() - 1)
                .coerceAtLeast(0)
                .coerceAtMost(currentPlaylistRepository.getSongs().size - 1)

            shuffledNewIndex = shuffledPlaylistOriginalIndexes.indexOf(originalNewIndex)
        }

        currentPlaylistRepository.changeToIndex(originalNewIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.changeToIndex(shuffledNewIndex, shouldAutoPlay)
    }

    override fun changeToFirstSong(shouldAutoPlay: Boolean) {
        val originalNewIndex : Int
        val shuffledNewIndex : Int

        if (isShuffleEnabled) {
            shuffledNewIndex = 0
            originalNewIndex = shuffledPlaylistOriginalIndexes[shuffledNewIndex]
        } else {
            originalNewIndex = 0
            shuffledNewIndex = shuffledPlaylistOriginalIndexes.indexOf(originalNewIndex)
        }

        currentPlaylistRepository.changeToIndex(originalNewIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.changeToIndex(shuffledNewIndex, shouldAutoPlay)
    }

    override fun changeToLastSong(shouldAutoPlay: Boolean) {
        val originalNewIndex : Int
        val shuffledNewIndex : Int

        if (isShuffleEnabled) {
            shuffledNewIndex = currentShuffledPlaylistRepository.getSongs().size
            originalNewIndex = shuffledPlaylistOriginalIndexes[shuffledNewIndex]
        } else {
            originalNewIndex = currentPlaylistRepository.getSongs().size
            shuffledNewIndex = shuffledPlaylistOriginalIndexes.indexOf(originalNewIndex)
        }

        currentPlaylistRepository.changeToIndex(originalNewIndex, shouldAutoPlay)
        currentShuffledPlaylistRepository.changeToIndex(shuffledNewIndex, shouldAutoPlay)
    }
}
