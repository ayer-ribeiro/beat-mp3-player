package dev.ayer.audioplayer.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class OnDestroyLifecycleObserver(
    private val lifecycle: Lifecycle,
    private val callback: () -> Unit
): DefaultLifecycleObserver {
    init {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            lifecycle.addObserver(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycle.removeObserver(this)
        callback()
    }
}