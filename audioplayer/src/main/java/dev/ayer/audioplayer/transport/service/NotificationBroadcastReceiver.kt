package dev.ayer.audioplayer.transport.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dev.ayer.audioplayer.util.ListenerWrapper
import java.lang.Exception

class NotificationBroadcastReceiver : BroadcastReceiver() {
    interface Listener {
        fun onPlayPauseRequested()
        fun onBackwardRequested()
        fun onForwardRequested()
    }

    val listeners = ListenerWrapper<Listener>()
    private val listenerWrapperOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    private val intentFilter = IntentFilter(NotificationBroadcastReceiverContainer.ACTION_PLAY_PAUSE).apply {
        addAction(NotificationBroadcastReceiverContainer.ACTION_SKIP_NEXT)
        addAction(NotificationBroadcastReceiverContainer.ACTION_SKIP_PREVIOUS)
    }

    fun register(context: Context) {
        context.registerReceiver(this, intentFilter)
    }

    fun unRegister(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationBroadcastReceiverContainer.ACTION_PLAY_PAUSE -> {
                listenerWrapperOwner.callListenersFunction { it.onPlayPauseRequested() }
            }
            NotificationBroadcastReceiverContainer.ACTION_SKIP_PREVIOUS -> {
                listenerWrapperOwner.callListenersFunction { it.onBackwardRequested() }
            }
            NotificationBroadcastReceiverContainer.ACTION_SKIP_NEXT -> {
                listenerWrapperOwner.callListenersFunction { it.onForwardRequested() }
            }
        }
    }
}
