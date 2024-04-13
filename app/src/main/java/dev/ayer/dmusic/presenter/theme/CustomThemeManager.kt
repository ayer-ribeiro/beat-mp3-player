package dev.ayer.dmusic.presenter.theme

import android.content.Context
import dev.ayer.audioplayer.util.ListenerWrapper
import dev.ayer.dmusic.data.UserThemePreferencesImpl
import dev.ayer.dmusic.domain.UserThemeListener
import dev.ayer.dmusic.entity.Theme

internal object CustomThemeManager {
    val listeners = ListenerWrapper<UserThemeListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    private lateinit var userThemePreferencesImpl: UserThemePreferencesImpl

    fun init(context: Context) {
        userThemePreferencesImpl = UserThemePreferencesImpl(context)
        userThemePreferencesImpl.listeners.addListener { theme ->
            listenerOwner.callListenersFunction {
                it.onChanged(theme)
            }
        }
    }

    fun getCurrentTheme(): Theme {
        return userThemePreferencesImpl.getTheme()
    }

    fun updateTheme(theme: Theme) {
        userThemePreferencesImpl.setTheme(theme)
    }
}
