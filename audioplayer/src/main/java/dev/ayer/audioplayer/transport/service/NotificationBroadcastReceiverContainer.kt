package dev.ayer.audioplayer.transport.service

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.ayer.audioplayer.transport.Player
import kotlinx.coroutines.launch

class NotificationBroadcastReceiverContainer(private val service: LifecycleService, private val player: Player) {

    companion object {
        const val ACTION_PLAY_PAUSE = "dev.ayer.notification_play_pause"
        const val ACTION_SKIP_PREVIOUS = "dev.ayer.notification_skip_previous"
        const val ACTION_SKIP_NEXT = "dev.ayer.notification_skip_next"
    }

    private val receiver = NotificationBroadcastReceiver()

    init {
        receiver.register(service)
        service.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                receiver.unRegister(service)
            }
        })

        receiver.listeners.addListener(service.lifecycle, object : NotificationBroadcastReceiver.Listener {
            override fun onPlayPauseRequested() {
                service.lifecycleScope.launch {
                    player.requestTogglePlayPause()
                }
            }

            override fun onBackwardRequested() {
                service.lifecycleScope.launch {
                    player.backwardRequested()
                }
            }

            override fun onForwardRequested() {
                service.lifecycleScope.launch {
                    player.forwardRequested()
                }
            }
        })
    }
}
