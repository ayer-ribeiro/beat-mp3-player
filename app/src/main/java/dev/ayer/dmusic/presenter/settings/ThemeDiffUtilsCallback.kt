package dev.ayer.dmusic.presenter.settings

import androidx.recyclerview.widget.DiffUtil
import dev.ayer.dmusic.entity.Theme

class ThemeDiffUtilsCallback: DiffUtil.ItemCallback<Theme>() {
    override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem == newItem
    }
}
