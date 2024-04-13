package dev.ayer.audioplayer.util

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import java.lang.IllegalStateException
import java.lang.RuntimeException

class Observable<T>(lifecycle: Lifecycle) {

    private val uiHandler = Handler(Looper.getMainLooper())

    private var intervalMillis: Long? = null
    private var action: (() -> T)? = null
    private var onUpdate: ((T) -> Unit)? = null

    private var currentRunnable: Runnable? = null

    init {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            OnDestroyLifecycleObserver(lifecycle) {
                this.pause()
            }
        }
    }

    fun intervalMillis(intervalMillis: Long): Observable<T> {
        this.intervalMillis = intervalMillis
        return this
    }

    fun onUpdate(onUpdate: (T) -> Unit): Observable<T> {
        this.onUpdate = onUpdate
        return this
    }

    fun subscribe(action: () -> T): Observable<T> {
        this.action = action
        resume()
        return this
    }

    fun resume() {
        val action = this.action ?: throw IllegalStateException("action should be set")

        val intervalMillis = this.intervalMillis ?: throw IllegalStateException("intervalMillis should be set")

        val newRunnable = Runnable {
            val actionResponse = action()
            this.onUpdate?.invoke(actionResponse)
            this.currentRunnable?.let { uiHandler.postDelayed(it, intervalMillis) }
        }

        this.currentRunnable = newRunnable
        uiHandler.postDelayed(newRunnable, intervalMillis)
    }

    fun pause(): Observable<T> {
        this.currentRunnable?.let {
            uiHandler.removeCallbacks(it)
        }
        return this
    }

    companion object {
        fun <T> create(lifecycle: Lifecycle): Observable<T> {
            return Observable(lifecycle)
        }
    }
}
