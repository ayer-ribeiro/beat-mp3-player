package dev.ayer.dmusic.presenter.library.songs

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
import dev.ayer.dmusic.presenter.library.LibraryMediaDiffUtilCallback
import dev.ayer.dmusic.presenter.library.artists.LibraryArtistsRecyclerViewAdapter
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong

class LibrarySongsRecyclerViewAdapter :
    ListAdapter<LibrarySong, LibrarySongsRecyclerViewAdapter.ViewHolder>(LibraryMediaDiffUtilCallback()) {

    fun interface OnClickListener {
        fun onClick(song: LibrarySong)
    }

    fun interface OnPlayLickedListener {
        fun onPlay(song: LibrarySong)
    }

    fun interface OnAddToNextInQueueListener {
        fun onAddToNextInQueue(song: LibrarySong)
    }

    fun interface OnAddToEndInQueueListener {
        fun onAddToEndInQueue(song: LibrarySong)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songNameTextView: TextView = view.findViewById(R.id.song_name)
        val artistNameTextView: TextView = view.findViewById(R.id.artist_name)
        val albumThumbImageView: ImageView = view.findViewById(R.id.album_thumb)
        val playingIndicator: View = view.findViewById(R.id.playing_indicator)
        val overflow: View = view.findViewById(R.id.overflow)
    }

    var onClickListener: OnClickListener? = null
    var currentPlayingSong: PlayerSong? = null
    var onPlayLickedListener: OnPlayLickedListener? = null
    var onAddToNextInQueueListener: OnAddToNextInQueueListener? = null
    var onAddToEndInQueueListener: OnAddToEndInQueueListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.library_songs_adapter_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = currentList[position]

        if (position == 0) {
            val margin = holder.itemView.context.resources.getDimensionPixelOffset(R.dimen.default_half_margin)
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin
        } else {
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
        }

        if (position == currentList.size - 1) {
            val margin = holder.itemView.context.resources.getDimensionPixelOffset(R.dimen.mini_player_recycler_view_end_space)
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = margin
        } else {
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
        }

        holder.songNameTextView.text = song.name
        holder.artistNameTextView.text = song.artist.name
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(song)
        }

        if (currentPlayingSong?.id?.androidId == song.id.androidId) {
            holder.playingIndicator.visibility = View.VISIBLE
        } else {
            holder.playingIndicator.visibility = View.GONE
        }

        holder.overflow.setOnClickListener {
            showMediaPopupMenu(holder.overflow, holder.overflow.context, song)
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
        song: LibrarySong
    ) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.media_list_item_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.play -> {
                    onPlayLickedListener?.onPlay(song)
                    true
                }
                R.id.add_to_next_in_queue -> {
                    onAddToNextInQueueListener?.onAddToNextInQueue(song)
                    true
                }
                R.id.add_to_end_queue -> {
                    onAddToEndInQueueListener?.onAddToEndInQueue(song)
                    true
                }
                else -> {
                    false
                }
            }
        }
        popup.show()
    }

    private fun LibrarySong.getAlbumArtUri(): Uri {
        val album = this.album
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, album.id.androidId)
    }
}
