package dev.ayer.dmusic.presenter.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.ayer.audioplayer.entity.Percentage
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.R
import dev.ayer.dmusic.presenter.customviews.BigMiniPlayerView
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import kotlinx.coroutines.launch

class PlayerActivity : CustomThemeBaseActivity() {

    private var player: Player? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.ServiceBinder
            player = binder.service.player
            observePlayer(player!!)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    private lateinit var currentPlaylistAdapter: PlayerCurrentPlaylistAdapter
    private lateinit var headerAdapter: PlayerHeaderRecyclerViewAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        recycler = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)

        currentPlaylistAdapter = PlayerCurrentPlaylistAdapter()
        headerAdapter = PlayerHeaderRecyclerViewAdapter()
        concatAdapter = ConcatAdapter(headerAdapter, currentPlaylistAdapter)


        setupPlayerHeaderRecyclerViewAdapterSeekBar(headerAdapter)
        setupMiniPlayerListeners(headerAdapter)
        setupRecyclerView(recycler)
        setupPlayerCurrentPlaylistAdapter(currentPlaylistAdapter)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.itemAnimator = null
        recycler.adapter = concatAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupPlayerCurrentPlaylistAdapter(
        playlistAdapter: PlayerCurrentPlaylistAdapter,
    ) {
        playlistAdapter.onClickListener = PlayerCurrentPlaylistAdapter.OnClickListener { _, index ->
            lifecycleScope.launch {
                player?.changeToSongInCurrentPlaylist(index)
            }
        }

        playlistAdapter.onRemoveClickListener = PlayerCurrentPlaylistAdapter.OnRemoveClickListener { _, index ->
            player?.removeFromCurrentPlaylistSongAt(index)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun setupMiniPlayerListeners(headerAdapter: PlayerHeaderRecyclerViewAdapter) {
        headerAdapter.onSeekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }

                lifecycleScope.launch {
                    player?.seekTo(Percentage.Companion.fromMaxScale(progress, headerAdapter.maxProgress))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        }
        headerAdapter.onShuffleButtonClicked = BigMiniPlayerView.OnShuffleButtonClicked {
            lifecycleScope.launch {
                player?.requestShuffleChange()
            }
        }

        headerAdapter.onPreviousButtonClicked = BigMiniPlayerView.OnPreviousButtonClicked {
            lifecycleScope.launch {
                player?.backwardRequested()
            }
        }

        headerAdapter.onPlayPauseButtonClicked = BigMiniPlayerView.OnPlayPauseButtonClicked {
            lifecycleScope.launch {
                player?.requestTogglePlayPause()
            }
        }

        headerAdapter.onForwardButtonClicked = BigMiniPlayerView.OnForwardButtonClicked {
            lifecycleScope.launch {
                player?.forwardRequested()
            }
        }

        headerAdapter.onRepeatButtonClicked = BigMiniPlayerView.OnRepeatButtonClicked {
            lifecycleScope.launch {
                player?.requestRepeatModeChange()
            }
        }
    }

    private fun setupPlayerHeaderRecyclerViewAdapterSeekBar(headerAdapter: PlayerHeaderRecyclerViewAdapter) {
        headerAdapter.maxProgress = 500
    }

    private fun observePlayer(player: Player) {
        player.observables.currentPlaylist.observe(this) { currentPlaylist ->
            lifecycleScope.launch {
                currentPlaylistAdapter.submitList(currentPlaylist)
            }
        }

        player.observables.currentSong.observe(this) { currentSong ->
            headerAdapter.song = currentSong
            headerAdapter.notifyItemChanged(0)
        }

        player.observables.currentIndex.observe(this) { index ->
            currentPlaylistAdapter.currentPlayingIndexSong = index
            currentPlaylistAdapter.notifyDataSetChanged()
        }

        player.observables.currentTimePercentage.observe(this) { percentage ->
            headerAdapter.progress = (percentage.getWithMaxScale(headerAdapter.maxProgress).toInt())
        }

        player.observables.isPlaying.observe(this) { isPlaying ->
            headerAdapter.isPlaying = isPlaying
            headerAdapter.notifyItemChanged(0)
        }

        player.observables.repeatMode.observe(this) { repeatMode ->
            headerAdapter.repeatMode = repeatMode
            headerAdapter.notifyItemChanged(0)
        }

        player.observables.isShuffleEnabled.observe(this) { isShuffleEnabled ->
            headerAdapter.isShuffleEnabled = isShuffleEnabled
            headerAdapter.notifyItemChanged(0)
        }
    }
}
