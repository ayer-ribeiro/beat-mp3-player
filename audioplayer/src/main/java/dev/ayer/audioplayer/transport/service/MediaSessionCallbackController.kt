package dev.ayer.audioplayer.transport.service

import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleCoroutineScope
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.transport.Player
import kotlinx.coroutines.launch

internal class MediaSessionCallbackController(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val mediaSessionCompat: MediaSessionCompat,
    private val player: Player,
): MediaSessionCompat.Callback() {

    init {
        mediaSessionCompat.setCallback(this)
    }

    override fun onPlay() {
        lifecycleScope.launch {
            player.requestTogglePlayPause()
        }
    }

    override fun onPause() {
        lifecycleScope.launch {
            player.requestTogglePlayPause()
        }
    }

    override fun onSkipToNext() {
        lifecycleScope.launch {
            player.forwardRequested()
        }
    }

    override fun onSkipToPrevious() {
        lifecycleScope.launch {
            player.backwardRequested()
        }
    }

    override fun onStop() {
        lifecycleScope.launch {
            player.requestTogglePlayPause()
        }
    }

    override fun onSeekTo(pos: Long) {
        lifecycleScope.launch {
            player.seekTo(PlayerTime.fromMilliSeconds(pos))
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        // TODO:
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        // TODO:
    }
}
