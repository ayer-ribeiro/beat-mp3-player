package dev.ayer.dmusic.presenter.player

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ayer.audioplayer.entity.RepeatMode
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.dmusic.R
import dev.ayer.dmusic.presenter.customviews.BigMiniPlayerView

class PlayerHeaderRecyclerViewAdapter: RecyclerView.Adapter<PlayerHeaderRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songNameTextView: TextView = view.findViewById(R.id.song_name)
        val artistNameTextView: TextView = view.findViewById(R.id.artist_name)
        val albumCoverImageView: ImageView = view.findViewById(R.id.album_cover)
        val miniPlayer: BigMiniPlayerView = view.findViewById(R.id.mini_player)
    }

    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null

    var onShuffleButtonClicked: BigMiniPlayerView.OnShuffleButtonClicked? = null
    var onPreviousButtonClicked: BigMiniPlayerView.OnPreviousButtonClicked? = null
    var onPlayPauseButtonClicked: BigMiniPlayerView.OnPlayPauseButtonClicked? = null
    var onForwardButtonClicked: BigMiniPlayerView.OnForwardButtonClicked? = null
    var onRepeatButtonClicked: BigMiniPlayerView.OnRepeatButtonClicked? = null

    var progress: Int = 0
        set(value) {
            field = value
            miniPlayerView?.progress = value
        }

    var maxProgress: Int = 100
    var song: PlayerSong? = null
    var isPlaying = false
    var isShuffleEnabled = false
    var repeatMode = RepeatMode.NONE

    private var miniPlayerView: BigMiniPlayerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.player_header_adapter_view_holder,
            parent,
            false
        )
        val viewHolder = ViewHolder(view)
        miniPlayerView = viewHolder.miniPlayer

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.songNameTextView.text = song?.name ?: context.getString(R.string.unknown_song)
        holder.artistNameTextView.text = song?.artist?.artistName ?: context.getString(R.string.unknown_artist)

        Glide.with(context)
            .load(song?.getAlbumArtUri())
            .centerCrop()
            .into(holder.albumCoverImageView)


        holder.miniPlayer.maxSeekBar = maxProgress
        holder.miniPlayer.progress = progress

        holder.miniPlayer.onSeekBarChangeListener = onSeekBarChangeListener
        holder.miniPlayer.onShuffleButtonClicked = onShuffleButtonClicked
        holder.miniPlayer.onPreviousButtonClicked = onPreviousButtonClicked
        holder.miniPlayer.onPlayPauseButtonClicked = onPlayPauseButtonClicked
        holder.miniPlayer.onForwardButtonClicked = onForwardButtonClicked
        holder.miniPlayer.onRepeatButtonClicked = onRepeatButtonClicked

        when(repeatMode) {
            RepeatMode.REPEAT_ONE -> holder.miniPlayer.setRepeatOneButton()
            RepeatMode.REPEAT_ALL -> holder.miniPlayer.setRepeatAllButton()
            RepeatMode.NONE -> holder.miniPlayer.setRepeatNoneButton()
            null -> holder.miniPlayer.setRepeatNoneButton()
        }

        if (isShuffleEnabled) {
            holder.miniPlayer.setShuffleActiveButton()
        } else {
            holder.miniPlayer.setShuffleInactiveButton()
        }

        if (isPlaying) {
            holder.miniPlayer.setPauseButton()
        } else {
            holder.miniPlayer.setPlayButton()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    private fun PlayerSong.getAlbumArtUri(): Uri? {
        val album = this.album ?: return null
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, album.androidId)
    }
}