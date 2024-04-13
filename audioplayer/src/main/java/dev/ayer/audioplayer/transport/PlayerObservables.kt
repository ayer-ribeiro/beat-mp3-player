package dev.ayer.audioplayer.transport

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import dev.ayer.audioplayer.domain.playerstate.PlayerState
import dev.ayer.audioplayer.domain.playerstate.PlayerStateListener
import dev.ayer.audioplayer.domain.playlistmanager.PlaylistManager
import dev.ayer.audioplayer.domain.playlistmanager.PlaylistManagerListener
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.entity.media.PlayerSong
import kotlinx.coroutines.launch

class PlayerObservables(
    private val lifecycle: Lifecycle,
    private val playerState: PlayerState,
    private val playlistManager: PlaylistManager,
) {

    private val _currentSong = MutableLiveData<PlayerSong?>()
    val currentSong: LiveData<PlayerSong?> = _currentSong

    private val _currentIndex = MutableLiveData<Int>()
    val currentIndex: LiveData<Int> = _currentIndex

    private val _currentPlaylist = MutableLiveData<List<PlayerSong>>()
    val currentPlaylist: LiveData<List<PlayerSong>> = _currentPlaylist

    private val _currentTimeMillis = MutableLiveData<Long>()
    val currentTimeMillis: LiveData<Long> = _currentTimeMillis

    private val _currentTimePercentage = MutableLiveData<Percentage>()
    val currentTimePercentage: LiveData<Percentage> = _currentTimePercentage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _isShuffleEnabled = MutableLiveData<Boolean>()
    val isShuffleEnabled: LiveData<Boolean> = _isShuffleEnabled

    private val _repeatMode = MutableLiveData<RepeatMode>()
    val repeatMode: LiveData<RepeatMode> = _repeatMode

    init {
        setupInitialValues()
        setupPlaylistManagerListener()
        setupPlayerStateListener()
    }

    private fun setupInitialValues() {
        playlistManager.getCurrentSong()?.let {
            _currentSong.postValue(playlistManager.getCurrentSong())
        }

        _currentIndex.postValue(playlistManager.getCurrentIndex())
        _currentPlaylist.postValue(playlistManager.getCurrentPlaylist())

        lifecycle.coroutineScope.launch {
            _currentTimeMillis.postValue(playerState.getCurrentSongCurrentTime().timeMillis)
            _currentTimePercentage.postValue(playerState.getCurrentSongPercentage() )
            _isLoading.postValue(playerState.isLoading())
            _isPlaying.postValue(playerState.isPlaying())
        }

        _isShuffleEnabled.postValue(playerState.isShuffleEnabled())
        _repeatMode.postValue(playerState.getRepeatMode())
    }

    private fun setupPlayerStateListener() {
        playerState.listeners.addListener(lifecycle, object : PlayerStateListener {
            override fun onCurrentTimeChangedMillis(timeMillis: Long, percentage: Percentage) {
                _currentTimeMillis.postValue(timeMillis)
                _currentTimePercentage.postValue(percentage)
            }

            override fun onLoadingChanged(loading: Boolean) {
                _isLoading.postValue(loading)
            }

            override fun onSongPlaying() {
                _isPlaying.postValue(true)
            }

            override fun onSongPaused() {
                _isPlaying.postValue(false)
            }

            override fun onSongEnded() {
                _isPlaying.postValue(false)
            }

            override fun onSongError() {
                _isPlaying.postValue(false)
            }

            override fun onShuffleChanged(isShuffleEnabled: Boolean) {
                _isShuffleEnabled.postValue(isShuffleEnabled)
            }

            override fun onRepeatModeChanged(mode: RepeatMode) {
                _repeatMode.postValue(mode)
            }
        })
    }

    private fun setupPlaylistManagerListener() {
        playlistManager.listeners.addListener(lifecycle, object : PlaylistManagerListener {
            override fun onCurrentSongChanged(song: PlayerSong, index: Int) {
                _currentSong.postValue(song)
            }

            override fun onCurrentIndexChanged(index: Int) {
                _currentIndex.postValue(index)
            }

            override fun onChanged(songs: List<PlayerSong>) {
                _currentPlaylist.postValue(songs)
            }

            override fun onSongRemoved(song: PlayerSong, index: Int) {}
            override fun onSongAdded(song: PlayerSong, index: Int) {}

        })
    }
}
