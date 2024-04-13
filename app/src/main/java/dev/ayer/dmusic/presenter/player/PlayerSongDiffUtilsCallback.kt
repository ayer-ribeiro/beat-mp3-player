package dev.ayer.dmusic.presenter.player

import androidx.recyclerview.widget.DiffUtil
import dev.ayer.audioplayer.entity.media.PlayerSong

class PlayerSongDiffUtilsCallback: DiffUtil.ItemCallback<PlayerSong>() {
    override fun areItemsTheSame(oldItem: PlayerSong, newItem: PlayerSong): Boolean {
        return oldItem.id.androidId == newItem.id.androidId
    }

    override fun areContentsTheSame(oldItem: PlayerSong, newItem: PlayerSong): Boolean {
        return false
    }
}
