package dev.ayer.audioplayer.transport

import android.content.Context
import androidx.lifecycle.Lifecycle
import dev.ayer.audioplayer.data.CurrentPlaylistRepositoryImpl
import dev.ayer.audioplayer.data.ExoPlayerRepository
import dev.ayer.audioplayer.data.UserPreferencesRepositoryImpl
import dev.ayer.audioplayer.domain.*
import dev.ayer.audioplayer.domain.PlayerLoader
import dev.ayer.audioplayer.domain.SeekRequestManager
import dev.ayer.audioplayer.domain.ShuffleChangeRequestManager
import dev.ayer.audioplayer.domain.SongChangeManager
import dev.ayer.audioplayer.domain.TogglePlayPauseManager
import dev.ayer.audioplayer.domain.playlistmanager.PlaylistManagerImpl
import dev.ayer.audioplayer.domain.playerstate.PlayerStateImpl
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.domain.repository.CurrentPlaylistRepository
import dev.ayer.audioplayer.domain.repository.PlayerRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.domain.usecases.forward.CanChangeToNextSongUseCase
import dev.ayer.audioplayer.domain.usecases.backward.CanChangeToPreviousSongUseCase
import dev.ayer.audioplayer.domain.usecases.backward.ShouldChangeToLastPlaylistSongOnBackwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.backward.ShouldRewindSongOnBackwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.forward.ShouldChangeToFirstPlaylistSongOnForwardRequestUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldChangeToFirstPlaylistSongOnSongCompletedUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldGoToNextSongOnSongCompletedUseCase
import dev.ayer.audioplayer.domain.usecases.songcompleted.ShouldRepeatSongOnSongCompletedUseCase

class Player(
    lifecycle: Lifecycle,
    context: Context
) {
    private val playerRepository: PlayerRepository = ExoPlayerRepository(lifecycle, context)
    private val currentPlaylistRepository: CurrentPlaylistRepository = CurrentPlaylistRepositoryImpl()
    private val currentShuffledPlaylistRepository: CurrentPlaylistRepository = CurrentPlaylistRepositoryImpl()
    private val userPreferencesRepository: UserPreferencesRepository = UserPreferencesRepositoryImpl(context)

    private val currentPlaylistFacade = CurrentPlaylistFacade(
        lifecycle,
        currentPlaylistRepository,
        currentShuffledPlaylistRepository,
        userPreferencesRepository
    )

    private val playlistManager = PlaylistManagerImpl(
        lifecycle,
        currentPlaylistFacade,
    )

    private val playerState = PlayerStateImpl(
        lifecycle,
        playerRepository,
        userPreferencesRepository
    )

    val observables = PlayerObservables(
        lifecycle,
        playerState,
        playlistManager
    )

    private val seekRequestManager = SeekRequestManager(playerRepository)
    private val togglePlayPauseManager = TogglePlayPauseManager(playerRepository)
    private val shuffleChangeRequestManager = ShuffleChangeRequestManager(userPreferencesRepository)
    private val repeatModeRequestManager = RepeatModeRequestManager(userPreferencesRepository)

    private val playerLoader = PlayerLoader(
        lifecycle,
        playerRepository,
        currentPlaylistFacade
    )

    private val songChangeManager = SongChangeManager(
        lifecycle,
        playerRepository,
        currentPlaylistFacade,
        CanChangeToNextSongUseCase(currentPlaylistFacade),
        CanChangeToPreviousSongUseCase(currentPlaylistFacade),
        ShouldChangeToLastPlaylistSongOnBackwardRequestUseCase(currentPlaylistFacade),
        ShouldChangeToFirstPlaylistSongOnForwardRequestUseCase(currentPlaylistFacade),
        ShouldRewindSongOnBackwardRequestUseCase(playerRepository),
        ShouldChangeToFirstPlaylistSongOnSongCompletedUseCase(currentPlaylistFacade, userPreferencesRepository),
        ShouldGoToNextSongOnSongCompletedUseCase(currentPlaylistFacade, userPreferencesRepository),
        ShouldRepeatSongOnSongCompletedUseCase(userPreferencesRepository)
    )

    suspend fun isPlaying(): Boolean {
        return playerState.isPlaying()
    }

    suspend fun isLoading(): Boolean {
        return playerState.isLoading()
    }

    suspend fun playSongs(songs: List<PlayerSong>, index: Int) {
        playlistManager.setupPlaylist(songs, index, true)
    }

    fun addAsNextToQueue(songs: List<PlayerSong>) {
        playlistManager.addAsNextToQueue(songs)
    }

    fun addToEndOfQueue(songs: List<PlayerSong>) {
        playlistManager.addToEndOfQueue(songs)
    }

    fun removeFromCurrentPlaylistSongAt(index: Int) {
        playlistManager.removeSongAt(index)
    }

    suspend fun forwardRequested() {
        songChangeManager.forwardRequested()
    }

    suspend fun backwardRequested() {
        songChangeManager.backwardRequested()
    }

    suspend fun changeToSongInCurrentPlaylist(index: Int) {
        songChangeManager.changeToSongInCurrentPlaylist(index)
    }

    suspend fun seekTo(time: PlayerTime) {
        seekRequestManager.seekTo(time)
    }

    suspend fun seekTo(percentage: Percentage) {
        seekRequestManager.seekTo(percentage)
    }

    suspend fun stop() {
        playerRepository.pause()
    }

    fun requestShuffleChange() {
        shuffleChangeRequestManager.requestShuffleChange()
    }

    suspend fun requestTogglePlayPause() {
        togglePlayPauseManager.togglePlayPause()
    }

    fun requestRepeatModeChange() {
        repeatModeRequestManager.requestRepeatModeChange()
    }
}
