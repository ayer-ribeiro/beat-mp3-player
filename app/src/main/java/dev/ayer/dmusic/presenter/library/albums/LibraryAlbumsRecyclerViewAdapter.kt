package dev.ayer.dmusic.presenter.library.albums

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
import dev.ayer.dmusic.R
import dev.ayer.dmusic.presenter.library.LibraryMediaDiffUtilCallback
import dev.ayer.medialibrary.entity.media.LibraryAlbum

class LibraryAlbumsRecyclerViewAdapter :
    ListAdapter<LibraryAlbum, LibraryAlbumsRecyclerViewAdapter.ViewHolder>(LibraryMediaDiffUtilCallback()) {

    fun interface OnClickListener {
        fun onClick(song: LibraryAlbum)
    }

    fun interface OnPlayLickedListener {
        fun onPlay(song: LibraryAlbum)
    }

    fun interface OnAddToNextInQueueListener {
        fun onAddToNextInQueue(song: LibraryAlbum)
    }

    fun interface OnAddToEndInQueueListener {
        fun onAddToEndInQueue(song: LibraryAlbum)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val albumNameTextView: TextView = view.findViewById(R.id.album_name)
        val albumInfoTextView: TextView = view.findViewById(R.id.album_info)
        val albumThumbImageView: ImageView = view.findViewById(R.id.album_thumb)
        val overflow: View = view.findViewById(R.id.overflow)
    }

    var onClickListener: OnClickListener? = null
    var onPlayLickedListener: OnPlayLickedListener? = null
    var onAddToNextInQueueListener: OnAddToNextInQueueListener? = null
    var onAddToEndInQueueListener: OnAddToEndInQueueListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.library_albums_adapter_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = currentList[position]

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

        holder.albumNameTextView.text = album.name
        if (album.releaseYear <= 0) {
            holder.albumInfoTextView.text = album.artist.name
        } else {
            holder.albumInfoTextView.text = "${album.artist.name} - ${album.releaseYear}"
        }

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(album)
        }

        holder.overflow.setOnClickListener {
            showMediaPopupMenu(holder.overflow, holder.overflow.context, album)
        }

        Glide
            .with(holder.albumThumbImageView)
            .load(album.getAlbumUri())
            .centerCrop()
            .into(holder.albumThumbImageView)
    }

    private fun showMediaPopupMenu(
        view: View,
        context: Context,
        album: LibraryAlbum
    ) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.media_list_item_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.play -> {
                    onPlayLickedListener?.onPlay(album)
                    true
                }
                R.id.add_to_next_in_queue -> {
                    onAddToNextInQueueListener?.onAddToNextInQueue(album)
                    true
                }
                R.id.add_to_end_queue -> {
                    onAddToEndInQueueListener?.onAddToEndInQueue(album)
                    true
                }
                else -> {
                    false
                }
            }
        }
        popup.show()
    }

    private fun LibraryAlbum.getAlbumUri(): Uri {
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, this.id.androidId)
    }
}
