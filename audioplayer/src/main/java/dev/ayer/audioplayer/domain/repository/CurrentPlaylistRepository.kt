package dev.ayer.audioplayer.domain.repository

import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.domain.repository.listeners.PlaylistRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper

internal interface CurrentPlaylistRepository {
    val listeners: ListenerWrapper<PlaylistRepositoryListener>

    fun setup(songs: List<PlayerSong>, currentIndex: Int, shouldAutoPlay: Boolean)
    fun getSongs(): List<PlayerSong>
    fun getCurrentIndex(): Int
    fun getCurrentSong(): PlayerSong?

    fun hasNextSong(): Boolean
    fun hasPreviousSong(): Boolean

    fun getFirstSong(): PlayerSong?
    fun getLastSong(): PlayerSong?
    fun getNextSong(): PlayerSong?
    fun getPreviousSong(): PlayerSong?

    fun addSongsToNextIndex(songs: List<PlayerSong>)
    fun addSongsToEnd(songs: List<PlayerSong>)
    fun removeSongAtIndex(index: Int)

    fun changeToIndex(index: Int, shouldAutoPlay: Boolean)
    fun changeToNext(shouldAutoPlay: Boolean)
    fun changeToPrevious(shouldAutoPlay: Boolean)
    fun changeToFirstSong(shouldAutoPlay: Boolean)
    fun changeToLastSong(shouldAutoPlay: Boolean)
}