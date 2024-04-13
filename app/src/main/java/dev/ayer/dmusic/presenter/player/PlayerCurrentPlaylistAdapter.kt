package dev.ayer.dmusic.presenter.player

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.dmusic.R
import dev.ayer.medialibrary.entity.media.LibrarySong

class PlayerCurrentPlaylistAdapter :
    ListAdapter<PlayerSong, PlayerCurrentPlaylistAdapter.ViewHolder>(PlayerSongDiffUtilsCallback()) {

    fun interface OnClickListener {
        fun onClick(song: PlayerSong, index: Int)
    }

    fun interface OnRemoveClickListener {
        fun onRemoveClicked(song: PlayerSong, index: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songNameTextView: TextView = view.findViewById(R.id.song_name)
        val artistNameTextView: TextView = view.findViewById(R.id.artist_name)
        val albumThumbImageView: ImageView = view.findViewById(R.id.album_thumb)
        val playingIndicator: View = view.findViewById(R.id.playing_indicator)
        val overflow: View = view.findViewById(R.id.overflow)
    }

    var onClickListener: OnClickListener? = null
    var onRemoveClickListener: OnRemoveClickListener? = null
    var currentPlayingIndexSong: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.player_current_playlist_adapter_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = currentList[position]
        val context = holder.itemView.context

        holder.songNameTextView.text = song.name ?: context.getString(R.string.unknown_song)
        holder.artistNameTextView.text = song.artist?.artistName ?: context.getString(R.string.unknown_artist)
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(song, position)
        }

        if (currentPlayingIndexSong == position) {
            holder.playingIndicator.visibility = View.VISIBLE
        } else {
            holder.playingIndicator.visibility = View.GONE
        }

        holder.overflow.setOnClickListener {
            showMediaPopupMenu(holder.overflow, holder.overflow.context, song, position)
        }

        Glide
            .with(holder.albumThumbImageView)
            .load(song.getAlbumArtUri())
            .centerCrop()
            .into(holder.albumThumbImageView)
    }

    private fun showMediaPopupMenu(
        view: View,
        context: Context,
        song: PlayerSong,
        index: Int,
    ) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.player_list_item_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remove -> {
                    onRemoveClickListener?.onRemoveClicked(song, index)
                    true
                }
                else -> {
                    false
                }
            }
        }
        popup.show()
    }

    private fun PlayerSong.getAlbumArtUri(): Uri? {
        val album = this.album ?: return null
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, album.androidId)
    }
}
