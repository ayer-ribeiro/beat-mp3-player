package dev.ayer.audioplayer.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlayerRepository
import dev.ayer.audioplayer.domain.usecases.backward.CanChangeToPreviousSongUseCase
import dev.ayer.audioplayer.domain.usecases.backward.ShouldChangeToLastPlaylistSongOnBackwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.backward.ShouldRewindSongOnBackwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.forward.CanChangeToNextSongUseCase
import dev.ayer.audioplayer.domain.usecases.forward.ShouldChangeToFirstPlaylistSongOnForwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldChangeToFirstPlaylistSongOnSongCompletedUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldGoToNextSongOnSongCompletedUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldRepeatSongOnSongCompletedUseCase
import dev.ayer.audioplayer.domain.repository.listeners.PlayerRepositoryListener
import kotlinx.coroutines.launch

internal class SongChangeManager(
    private val lifecycle: Lifecycle,
    private val playerRepository: PlayerRepository,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val canChangeToNextSongUseCase: CanChangeToNextSongUseCase,
    private val canChangeToPreviousSongUseCase: CanChangeToPreviousSongUseCase,
    private val shouldChangeToLastPlaylistSongOnBackwardRequestUseCase: ShouldChangeToLastPlaylistSongOnBackwardRequestUseCase,
    private val shouldChangeToFirstPlaylistSongOnForwardRequestUseCase: ShouldChangeToFirstPlaylistSongOnForwardRequestUseCase,
    private val shouldRewindSongOnBackwardRequestUseCase: ShouldRewindSongOnBackwardRequestUseCase,
    private val shouldChangeToFirstPlaylistSongOnSongCompletedUseCase: ShouldChangeToFirstPlaylistSongOnSongCompletedUseCase,
    private val shouldGoToNextSongOnSongCompletedUseCase: ShouldGoToNextSongOnSongCompletedUseCase,
    private val shouldRepeatSongOnSongCompletedUseCase: ShouldRepeatSongOnSongCompletedUseCase,
) {
    init {
        playerRepository.listeners.addListener(lifecycle, object : PlayerRepositoryListener {
            override fun onCurrentTimeChangedMillis(timeMillis: Long, percentage: Percentage) {}
            override fun onLoadingChanged(loading: Boolean) {}
            override fun onSongPlaying() {}
            override fun onSongPaused() {}
            override fun onSongError() {}
            override fun onSongEnded() {
                lifecycle.coroutineScope.launch {
                    onCurrentSongEnded()
                }
            }
        })
    }

    fun changeToSongInCurrentPlaylist(index: Int) {
        currentPlaylistRepository.changeToIndex(index, true)
    }

    fun forwardRequested() {
        if (canChangeToNextSongUseCase.canChangeToNext()) {
            currentPlaylistRepository.changeToNext(true)
            return
        }

        if (shouldChangeToFirstPlaylistSongOnForwardRequestUseCase.shouldChangeToFirstSong()) {
            currentPlaylistRepository.changeToFirstSong(true)
            return
        }
    }

    suspend fun backwardRequested() {
        if (shouldRewindSongOnBackwardRequestUseCase.shouldRewind()) {
            playerRepository.seekTo(PlayerTime.fromMilliSeconds(0))
            return
        }

        if (canChangeToPreviousSongUseCase.canChangeToPreviousSong()) {
            currentPlaylistRepository.changeToPrevious(true)
            return
        }

        if (shouldChangeToLastPlaylistSongOnBackwardRequestUseCase.shouldChangeToLastSong()) {
            currentPlaylistRepository.changeToLastSong(true)
            return
        }
    }

    private suspend fun onCurrentSongEnded() {
        if (shouldRepeatSongOnSongCompletedUseCase.shouldRepeatSong()) {
            playerRepository.seekTo(PlayerTime.fromMilliSeconds(0))
            playerRepository.resume()
            return
        }

        if (shouldGoToNextSongOnSongCompletedUseCase.shouldGoToNextSong()) {
            currentPlaylistRepository.changeToNext(true)
            return
        }

        if (shouldChangeToFirstPlaylistSongOnSongCompletedUseCase.shouldGoToFirstSong()) {
            currentPlaylistRepository.changeToFirstSong(true)
            return
        }
    }
}
