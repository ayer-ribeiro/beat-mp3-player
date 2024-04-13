package dev.ayer.dmusic.domain

import dev.ayer.audioplayer.util.ListenerWrapper
import dev.ayer.dmusic.entity.Theme

interface UserThemePreferences {
    val listeners: ListenerWrapper<UserThemeListener>

    fun getTheme(): Theme
    fun setTheme(theme: Theme)
}
