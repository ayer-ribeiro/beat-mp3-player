package dev.ayer.audioplayer.util

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle

class ListenerWrapper<T> {

    class ListenerWrapperOwner<T>(private val listenerWrapper: ListenerWrapper<T>) {
        fun callListenersFunction(function: (T) -> Unit) {
            synchronized(listenerWrapper.lockObject) {
                val listenersCopy = ArrayList(this.listenerWrapper.listeners)
                listenersCopy.forEach { listener ->
                    listenerWrapper.uiHandler.post {
                        function(listener)
                    }
                }
            }
        }
    }

    private val lockObject = Object()
    private val listeners = ArrayList<T>()
    private val uiHandler = Handler(Looper.getMainLooper())

    fun addListener(lifecycle: Lifecycle, listener: T) {
        synchronized(lockObject) {
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                return
            }

            OnDestroyLifecycleObserver(lifecycle) {
                removeListener(listener)
            }

            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
        }
    }

    fun addListener(listener: T) {
        synchronized(lockObject) {
            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
        }
    }

    fun removeListener(listener: T) {
        synchronized(lockObject) {
            listeners.remove(listener)
        }
    }
}
