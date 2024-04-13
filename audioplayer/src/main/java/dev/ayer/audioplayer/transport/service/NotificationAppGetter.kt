package dev.ayer.audioplayer.transport.service

import android.content.Context

interface PlayerApp {
    fun getNotificationIconDrawableId(): Int
}

internal class NotificationAppGetter {
    fun getNotificationIconDrawableId(context: Context): Int {
        val applicationContext = context.applicationContext
        val playerApp = applicationContext as PlayerApp
        return playerApp.getNotificationIconDrawableId()
    }
}
