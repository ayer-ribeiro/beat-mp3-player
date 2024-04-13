package dev.ayer.audioplayer.domain.playerstate

import androidx.lifecycle.Lifecycle
import dev.ayer.audioplayer.domain.repository.PlayerRepository
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlayerRepositoryListener
import dev.ayer.audioplayer.domain.repository.listeners.UserPreferencesRepositoryListener
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.util.ListenerWrapper

internal class PlayerStateImpl(
    lifecycle: Lifecycle,
    private val playerRepository: PlayerRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): PlayerState {
    init {
        playerRepository.listeners.addListener(lifecycle, object: PlayerRepositoryListener {
            override fun onCurrentTimeChangedMillis(timeMillis: Long, percentage: Percentage) {
                listenerWrapperOwner.callListenersFunction {
                    it.onCurrentTimeChangedMillis(timeMillis, percentage)
                }
            }

            override fun onLoadingChanged(loading: Boolean) {
                listenerWrapperOwner.callListenersFunction {
                    it.onLoadingChanged(loading)
                }
            }

            override fun onSongPlaying() {
                listenerWrapperOwner.callListenersFunction {
                    it.onSongPlaying()
                }
            }

            override fun onSongPaused() {
                listenerWrapperOwner.callListenersFunction {
                    it.onSongPaused()
                }
            }

            override fun onSongEnded() {
                listenerWrapperOwner.callListenersFunction {
                    it.onSongEnded()
                }
            }

            override fun onSongError() {
                listenerWrapperOwner.callListenersFunction {
                    it.onSongError()
                }
            }
        })

        userPreferencesRepository.listeners.addListener(lifecycle, object: UserPreferencesRepositoryListener {
            override fun onShuffleChanged(isEnabled: Boolean) {
                listenerWrapperOwner.callListenersFunction {
                    it.onShuffleChanged(isEnabled)
                }
            }

            override fun onRepeatModeChanged(mode: RepeatMode) {
                listenerWrapperOwner.callListenersFunction {
                    it.onRepeatModeChanged(mode)
                }
            }
        })
    }

    override val listeners = ListenerWrapper<PlayerStateListener>()
    private val listenerWrapperOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    override suspend fun getCurrentSongCurrentTime(): PlayerTime {
        return playerRepository.getCurrentTime()
    }

    override suspend fun getCurrentSongPercentage(): Percentage {
        return playerRepository.getCurrentPercentage()
    }

    override suspend fun getCurrentSongDuration(): PlayerTime {
        return playerRepository.getSongDuration()
    }

    override suspend fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override suspend fun isLoading(): Boolean {
        return playerRepository.isLoading()
    }

    override fun isShuffleEnabled(): Boolean {
        return userPreferencesRepository.isShuffleEnabled()
    }

    override fun getRepeatMode(): RepeatMode {
        return userPreferencesRepository.getRepeatMode()
    }
}
