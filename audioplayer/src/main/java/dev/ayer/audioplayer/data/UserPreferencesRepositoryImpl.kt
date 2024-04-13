package dev.ayer.audioplayer.data

import android.content.Context
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.domain.repository.UserPreferencesRepository
import dev.ayer.audioplayer.domain.repository.listeners.UserPreferencesRepositoryListener
import dev.ayer.audioplayer.util.ListenerWrapper

internal class UserPreferencesRepositoryImpl(context: Context): UserPreferencesRepository {

    private val sharedPref = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

    override val listeners = ListenerWrapper<UserPreferencesRepositoryListener>()
    private val listenerOwner = ListenerWrapper.ListenerWrapperOwner(listeners)

    override fun isShuffleEnabled(): Boolean {
        return sharedPref.getBoolean(SHARED_PREFS_SHUFFLE_KEY, false)
    }

    override fun getRepeatMode(): RepeatMode {
        val repeatId = sharedPref.getInt(SHARED_PREFS_REPEAT_KEY, 0)
        return RepeatMode.fromId(repeatId) ?: RepeatMode.NONE
    }

    override fun updateShuffleEnabled(isEnabled: Boolean) {
        val lastShuffleState = isShuffleEnabled()

        if (isEnabled != lastShuffleState) {
            listenerOwner.callListenersFunction {
                it.onShuffleChanged(isEnabled)
            }
        }

        sharedPref.edit().putBoolean(SHARED_PREFS_SHUFFLE_KEY, isEnabled).apply()
    }

    override fun updateRepeatMode(repeatMode: RepeatMode) {
        val lastRepeatMode = getRepeatMode()

        if (repeatMode != lastRepeatMode) {
            listenerOwner.callListenersFunction {
                it.onRepeatModeChanged(repeatMode)
            }
        }
        sharedPref.edit().putInt(SHARED_PREFS_REPEAT_KEY, repeatMode.id).apply()
    }

    companion object {
        const val SHARED_PREFS_FILE_NAME = "shuffle_and_repeat_preferences"
        const val SHARED_PREFS_REPEAT_KEY = "repeat_mode_preferences_key"
        const val SHARED_PREFS_SHUFFLE_KEY = "shuffle_preferences_key"
    }
}