package dev.ayer.dmusic.data

import android.content.Context
import dev.ayer.audioplayer.util.ListenerWrapper
import dev.ayer.dmusic.domain.UserThemeListener
import dev.ayer.dmusic.domain.UserThemePreferences
import dev.ayer.dmusic.entity.Theme

internal class UserThemePreferencesImpl(context: Context): UserThemePreferences {

    private val sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

    override val listeners = ListenerWrapper<UserThemeListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    override fun getTheme(): Theme {
        return Theme.fromId(
            sharedPref.getInt(SHARED_PREFS_THEME_KEY, -1)
        )
    }

    override fun setTheme(theme: Theme) {
        val lastTheme = getTheme()

        if (theme != lastTheme) {
            listenerOwner.callListenersFunction {
                it.onChanged(theme)
            }
        }
        sharedPref.edit().putInt(SHARED_PREFS_THEME_KEY, theme.id).apply()
    }

    companion object {
        const val SHARED_PREFS_FILE_NAME = "user_theme_preferences"
        const val SHARED_PREFS_THEME_KEY = "theme_preferences_key"
    }
}
