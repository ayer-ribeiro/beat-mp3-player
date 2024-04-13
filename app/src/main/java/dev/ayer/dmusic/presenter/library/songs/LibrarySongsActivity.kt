package dev.ayer.dmusic.presenter.library.songs

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.Banner
import dev.ayer.dmusic.R
import dev.ayer.dmusic.domain.LibraryToPlayerSongAdapter
import dev.ayer.dmusic.presenter.banner.BannerRecyclerViewAdapter
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.hasReadMediaPermission
import dev.ayer.dmusic.presenter.library.artists.LibraryArtistsRecyclerViewAdapter
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.dmusic.presenter.requestMediaPermission
import dev.ayer.dmusic.presenter.shouldShowRequestPermissionRationaleCompat
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.medialibrary.transport.Library
import kotlinx.coroutines.launch

internal class LibrarySongsActivity : CustomThemeBaseActivity() {

    private val library: Library by lazy {
        Library(this)
    }

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
    private lateinit var songsAdapter: LibrarySongsRecyclerViewAdapter
    private lateinit var bannerAdapter: BannerRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library_songs_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        recycler = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)

        songsAdapter = LibrarySongsRecyclerViewAdapter()
        bannerAdapter= BannerRecyclerViewAdapter(Banner.SONGS_LIST_TOP_NATIVE.adUnit)

        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)
        setupRecyclerView(recycler)
        setupRecyclerViewAdapter(songsAdapter)

        val adapter = ConcatAdapter(bannerAdapter, songsAdapter)

        recycler.adapter = adapter

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    songsAdapter.submitList(library.getSongsWithAlphabeticalOrder())
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        when {
            hasReadMediaPermission() -> {
                songsAdapter.submitList(library.getSongsWithAlphabeticalOrder())
            }
            shouldShowRequestPermissionRationaleCompat() -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            //showInContextUI(...)
        }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.requestMediaPermission()
            }
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

    private fun setupRecyclerViewAdapter(adapter: LibrarySongsRecyclerViewAdapter) {
        adapter.onClickListener = LibrarySongsRecyclerViewAdapter.OnClickListener { song ->
            lifecycleScope.launch {
                val songs = library.getSongsWithAlphabeticalOrder()
                val playerSongs = LibraryToPlayerSongAdapter().execute(songs)
                val currentPlayerSong = playerSongs.find { it.id.androidId == song.id.androidId }
                val currentSongIndex = playerSongs.indexOf(currentPlayerSong)
                player?.playSongs(playerSongs, currentSongIndex)
            }
        }

        adapter.onPlayLickedListener = LibrarySongsRecyclerViewAdapter.OnPlayLickedListener { song ->
            lifecycleScope.launch {
                val songs = library.getSongsWithAlphabeticalOrder()
                val playerSongs = LibraryToPlayerSongAdapter().execute(songs)
                val currentPlayerSong = playerSongs.find { it.id.androidId == song.id.androidId }
                val currentSongIndex = playerSongs.indexOf(currentPlayerSong)
                player?.playSongs(playerSongs, currentSongIndex)
            }
        }

        adapter.onAddToNextInQueueListener = LibrarySongsRecyclerViewAdapter.OnAddToNextInQueueListener { song ->
            val playerSong = LibraryToPlayerSongAdapter().execute(song)
            player?.addAsNextToQueue(listOf(playerSong))
        }

        adapter.onAddToEndInQueueListener = LibrarySongsRecyclerViewAdapter.OnAddToEndInQueueListener { song ->
            val playerSong = LibraryToPlayerSongAdapter().execute(song)
            player?.addToEndOfQueue(listOf(playerSong))
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this@LibrarySongsActivity)
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

            songsAdapter.currentPlayingSong = currentSong
            songsAdapter.notifyDataSetChanged()
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
