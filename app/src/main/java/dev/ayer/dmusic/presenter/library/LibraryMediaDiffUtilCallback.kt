package dev.ayer.dmusic.presenter.library

import androidx.recyclerview.widget.DiffUtil
import dev.ayer.medialibrary.entity.media.Media

class LibraryMediaDiffUtilCallback<T : Media<T>>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hasSameId(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hasSameId(newItem)
    }
}
