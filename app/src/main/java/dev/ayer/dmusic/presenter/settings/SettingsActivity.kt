package dev.ayer.dmusic.presenter.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.R
import dev.ayer.dmusic.entity.Theme
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.dmusic.presenter.theme.CustomThemeManager
import kotlinx.coroutines.launch

class SettingsActivity : CustomThemeBaseActivity() {

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

    private lateinit var miniPlayerView: MiniPlayerView
    private lateinit var miniPlayerCardView: CardView
    private lateinit var adapter: ColorSettingsAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        recycler = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)

        adapter = ColorSettingsAdapter()

        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)
        setupRecyclerView(recycler)
        setupRecyclerViewAdapter(recycler, adapter)

        adapter.selectedTheme = CustomThemeManager.getCurrentTheme()
        adapter.submitList(Theme.values().toMutableList())

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CustomThemeManager.listeners.addListener(lifecycle) { theme ->
            adapter.selectedTheme = theme
            adapter.notifyDataSetChanged()
        }
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

    private fun setupRecyclerViewAdapter(recyclerView: RecyclerView, adapter: ColorSettingsAdapter) {
        adapter.onClickListener = ColorSettingsAdapter.OnClickListener { theme ->
            CustomThemeManager.updateTheme(theme)
        }
        recyclerView.adapter = adapter
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this@SettingsActivity)
        recyclerView.setHasFixedSize(true)
    }

    private fun setupMiniPlayerSeekBar(miniPlayerView: MiniPlayerView) {
        miniPlayerView.maxSeekBar = 500
    }

    private fun setupMiniPlayerListeners(miniPlayerView: MiniPlayerView) {
        miniPlayerView.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        miniPlayerView.onPlayPauseButtonClicked = MiniPlayerView.OnPlayPauseButtonClicked {
            lifecycleScope.launch {
                player?.requestTogglePlayPause()
            }
        }

        miniPlayerView.onForwardButtonClicked = MiniPlayerView.OnForwardButtonClicked {
            lifecycleScope.launch {
                player?.forwardRequested()
            }
        }
    }

    private fun observePlayer(player: Player) {
        player.observables.currentSong.observe(this) { currentSong ->
            if(currentSong != null) {
                miniPlayerView.setSongName(currentSong.name ?: getString(R.string.unknown_song))
                miniPlayerView.setSongArtistName(currentSong.artist?.artistName ?: getString(R.string.unknown_artist))
                miniPlayerView.setAlbumThumbImageUri(currentSong.album?.androidId)
                miniPlayerCardView.visibility = View.VISIBLE
            } else {
                miniPlayerCardView.visibility = View.GONE
            }
        }

        player.observables.currentTimePercentage.observe(this) { percentage ->
            miniPlayerView.progress = (percentage.getWithMaxScale(miniPlayerView.maxSeekBar).toInt())
        }

        player.observables.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                miniPlayerView.setPauseButton()
            } else {
                miniPlayerView.setPlayButton()
            }
        }
    }
}
