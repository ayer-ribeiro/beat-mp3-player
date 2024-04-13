package dev.ayer.audioplayer.transport.service

import android.app.*
import android.app.Service.STOP_FOREGROUND_DETACH
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.media.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import dev.ayer.audioplayer.R
import dev.ayer.audioplayer.transport.Player

internal class NotificationCreator(
    service: LifecycleService,
    mediaSession: MediaSessionCompat,
    player: Player,
) {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val NOTIFICATION_CHANNEL_NAME = "Player"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Player Notification"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel(service)

        player.observables.currentSong.observe(service) { currentSong ->
            if (currentSong == null) {
                service.stopForeground()
                return@observe
            }
            showNotification(service, mediaSession, player.observables.isPlaying.value ?: false)
        }

        player.observables.isPlaying.observe(service) { isPlaying ->
            val currentSong = player.observables.currentSong.value

            if (currentSong == null) {
                service.stopForeground()
                return@observe
            }

            showNotification(service, mediaSession, isPlaying)
        }

        service.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                service.stopForeground()
            }
        })
    }

    private fun showNotification(
        service: LifecycleService,
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean
    ) {
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(service, mediaSession, isPlaying)

        if (isPlaying) {
            service.startForeground(NOTIFICATION_ID, notification)
        } else {
            service.stopForeground()
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(
        service: Service,
        mediaSession: MediaSessionCompat,
        isPlaying: Boolean,
    ): Notification {
        val smallIcon = NotificationAppGetter().getNotificationIconDrawableId(service)
        val mediaStyle = getMediaStyle(mediaSession)

        val playIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_PLAY
        )
        val pauseIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_PAUSE
        )
        val skipPreviousIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
        val skipNextIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
            service,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )

        val playAction = androidx.core.app.NotificationCompat.Action.Builder(
            R.drawable.outline_play_arrow_black_36, "Play", playIntent
        ).build()
        val pauseAction = androidx.core.app.NotificationCompat.Action.Builder(
            R.drawable.outline_pause_black_36, "Pause", pauseIntent
        ).build()
        val skipPreviousAction = androidx.core.app.NotificationCompat.Action.Builder(
            R.drawable.outline_skip_previous_black_36, "Previous", skipPreviousIntent
        ).build()
        val skipNextAction = androidx.core.app.NotificationCompat.Action.Builder(
            R.drawable.outline_skip_next_black_36, "Next", skipNextIntent
        ).build()

        val playPauseAction = if (isPlaying) pauseAction else playAction

        return androidx.core.app.NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
            .setStyle(mediaStyle)
            .setSmallIcon(smallIcon)
            .addAction(skipPreviousAction)
            .addAction(playPauseAction)
            .setSilent(true)
            .addAction(skipNextAction)
            .build()
    }

    private fun createNotificationChannel(service: Service) {
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance).apply {
                description = NOTIFICATION_CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getMediaStyle(mediaSessionCompat: MediaSessionCompat): NotificationCompat.MediaStyle {
        val mediaStyle = NotificationCompat.MediaStyle()

        mediaStyle.setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSessionCompat.sessionToken)
        return mediaStyle
    }
}

private fun LifecycleService.stopForeground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.stopForeground(STOP_FOREGROUND_DETACH)
    } else {
        @Suppress("DEPRECATION") this.stopForeground(false)
    }
}