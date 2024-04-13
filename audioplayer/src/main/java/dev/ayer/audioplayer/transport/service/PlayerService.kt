package dev.ayer.audioplayer.transport.service

import android.app.Service
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_BUTTON
import android.content.Intent.EXTRA_KEY_EVENT
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import android.view.KeyEvent.*
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.ayer.audioplayer.transport.Player
import kotlinx.coroutines.launch

class PlayerService : LifecycleService() {

    inner class ServiceBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

    // Binder given to clients
    private val iBinder: IBinder = ServiceBinder()
    private var notificationCreator: NotificationCreator? = null
    private var notificationBroadcastReceiver: NotificationBroadcastReceiverContainer? = null
    private var notificationStateController: NotificationStateController? = null
    private var mediaSessionCallbackController: MediaSessionCallbackController? = null

    var player: Player? = null
        private set

    override fun onBind(intent: Intent): IBinder {
        initializePlayer()
        super.onBind(intent)
        return iBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {

            if (ACTION_MEDIA_BUTTON == intent.action) {
                val keyEvent = intent.extras!![EXTRA_KEY_EVENT] as KeyEvent
                lifecycleScope.launch {
                    when (keyEvent.keyCode) {
                        KEYCODE_MEDIA_PLAY -> player?.requestTogglePlayPause()
                        KEYCODE_MEDIA_PAUSE -> player?.requestTogglePlayPause()
                        KEYCODE_MEDIA_PLAY_PAUSE -> player?.requestTogglePlayPause()
                        KEYCODE_MEDIA_NEXT -> player?.forwardRequested()
                        KEYCODE_MEDIA_PREVIOUS -> player?.backwardRequested()
                        KEYCODE_MEDIA_STOP -> player?.stop()
                    }
                }
            }
        }
        return Service.START_STICKY
    }

    private fun initializePlayer() {
        player = Player(lifecycle, this)
        val mediaSession = MediaSessionCompat(this, MEDIA_SESSION_TAG)
        notificationCreator = NotificationCreator(this, mediaSession, player!!)
        notificationBroadcastReceiver = NotificationBroadcastReceiverContainer(this, player!!)
        notificationStateController = NotificationStateController(this, mediaSession, player!!)
        mediaSessionCallbackController = MediaSessionCallbackController(lifecycleScope, mediaSession, player!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            player?.stop()
        }
    }

    companion object {
        const val MEDIA_SESSION_TAG = "Player Notification Media Session Tag"
    }
}

