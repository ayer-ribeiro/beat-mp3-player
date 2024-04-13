package dev.ayer.audioplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.Lifecycle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayer.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.ayer.audioplayer.entity.AndroidId
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.domain.repository.PlayerRepository
import dev.ayer.audioplayer.domain.repository.listeners.PlayerRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper
import dev.ayer.audioplayer.util.Observable
import dev.ayer.audioplayer.util.OnDestroyLifecycleObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

internal class ExoPlayerRepository(
    lifecycle: Lifecycle,
    private val context: Context,
): PlayerRepository {

    private val exoPlayer: SimpleExoPlayer

    override val listeners = ListenerWrapper<PlayerRepositoryListener>()
    private val listenerWrapperOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    init {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw RuntimeException("ExoPlayerRepository cannot be initialized with destroyed lifecycle")
        }

        exoPlayer = createExoplayer(context)
        setupListeners(lifecycle, exoPlayer)
    }

    private fun createExoplayer(context: Context): SimpleExoPlayer {
        val defaultTrackSelector = DefaultTrackSelector(null)
        val defaultLoadControl = DefaultLoadControl()
        return ExoPlayerFactory.newSimpleInstance(
            context,
            defaultTrackSelector,
            defaultLoadControl
        )
    }

    private fun setupListeners(lifecycle: Lifecycle, simpleExoPlayer: SimpleExoPlayer) {
        val observable = Observable
            .create<Long>(lifecycle)
            .intervalMillis(500L)
            .subscribe { simpleExoPlayer.currentPosition }
            .onUpdate { progress ->
                val duration = simpleExoPlayer.duration
                val percentage = Percentage.fromMaxScale(progress, duration)
                listenerWrapperOwner.callListenersFunction { listener ->
                    listener.onCurrentTimeChangedMillis(progress, percentage)
                }
            }

        val listener = object : EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) { }
            override fun onPositionDiscontinuity() {}

            override fun onLoadingChanged(isLoading: Boolean) {
                listenerWrapperOwner.callListenersFunction {
                    it.onLoadingChanged(isLoading)
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == STATE_READY) {
                    observable.resume()
                    listenerWrapperOwner.callListenersFunction {
                        it.onSongPlaying()
                    }
                }

                if (!playWhenReady && playbackState == STATE_READY) {
                    observable.pause()
                    listenerWrapperOwner.callListenersFunction {
                        it.onSongPaused()
                    }
                }

                if (playbackState == STATE_ENDED) {
                    observable.pause()
                    listenerWrapperOwner.callListenersFunction {
                        it.onSongEnded()
                    }
                }
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                observable.pause()
                listenerWrapperOwner.callListenersFunction {
                    it.onSongError()
                }
            }
        }

        observable.pause()
        simpleExoPlayer.addListener(listener)
        OnDestroyLifecycleObserver(lifecycle) {
            simpleExoPlayer.removeListener(listener)
        }
    }

    override suspend fun resume() = withContext(Dispatchers.Main) {
        exoPlayer.playWhenReady = true
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        exoPlayer.playWhenReady = false
    }

    override suspend fun loadSong(androidId: AndroidId, autoPlay: Boolean) {
        val audioSource: MediaSource = withContext(Dispatchers.IO) {
            val playerInfo: String = Util.getUserAgent(context, "ExoPlayerInfo")
            val dataSourceFactory = DefaultDataSourceFactory(
                context, playerInfo
            )

            return@withContext ExtractorMediaSource(
                androidId.toAndroidContentUri(),
                dataSourceFactory, DefaultExtractorsFactory(), null, null
            )
        }
        withContext(Dispatchers.Main) {
            exoPlayer.playWhenReady = autoPlay
            exoPlayer.prepare(audioSource)
        }
    }

    override suspend fun seekTo(time: PlayerTime) = withContext(Dispatchers.Main) {
        exoPlayer.seekTo(time.timeMillis)
    }

    override suspend fun seekTo(percentage: Percentage) = withContext(Dispatchers.Main) {
        val timeToSeek = percentage.getWithMaxScale(exoPlayer.duration.toDouble())
        exoPlayer.seekTo(timeToSeek.toLong())
    }

    override suspend fun isPlaying(): Boolean = withContext(Dispatchers.Main) {
        return@withContext exoPlayer.playbackState == STATE_READY && exoPlayer.playWhenReady
    }

    override suspend fun isLoading(): Boolean = withContext(Dispatchers.Main) {
        return@withContext exoPlayer.isLoading
    }

    override suspend fun isIdle(): Boolean  = withContext(Dispatchers.Main) {
        return@withContext exoPlayer.playbackState == STATE_IDLE
                || exoPlayer.playbackState == STATE_ENDED
    }

    override suspend fun getCurrentTime(): PlayerTime = withContext(Dispatchers.Main) {
        PlayerTime.fromMilliSeconds(exoPlayer.currentPosition)
    }

    override suspend fun getSongDuration(): PlayerTime = withContext(Dispatchers.Main) {
        return@withContext PlayerTime.fromMilliSeconds(exoPlayer.duration)
    }

    override suspend fun getCurrentPercentage(): Percentage = withContext(Dispatchers.Main) {
        val duration = getSongDuration().timeMillis
        if (duration == 0L) {
            return@withContext Percentage.fromMaxScale(0, 1)
        }

        return@withContext Percentage.fromMaxScale(
            getCurrentTime(),
            getSongDuration()
        )
    }

    private fun AndroidId.toAndroidContentUri(): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            androidId
        )
    }
}
