package dev.ayer.dmusic.presenter.library.artists

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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.Banner
import dev.ayer.dmusic.R
import dev.ayer.dmusic.domain.LibraryToPlayerSongAdapter
import dev.ayer.dmusic.presenter.artist.ArtistActivity
import dev.ayer.dmusic.presenter.banner.BannerRecyclerViewAdapter
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.library.albums.LibraryAlbumsRecyclerViewAdapter
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.medialibrary.transport.Library
import kotlinx.coroutines.launch

class LibraryArtistsActivity : CustomThemeBaseActivity() {

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
    private lateinit var artistsAdapter: LibraryArtistsRecyclerViewAdapter
    private lateinit var bannerAdapter: BannerRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library_artists_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        recycler = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)

        artistsAdapter = LibraryArtistsRecyclerViewAdapter()
        bannerAdapter = BannerRecyclerViewAdapter(Banner.ARTISTS_LIST_TOP_NATIVE.adUnit)

        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)
        setupRecyclerView(recycler)
        setupRecyclerViewAdapter(artistsAdapter)
        artistsAdapter.submitList(library.getArtistsWithAlphabeticalOrder())

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recycler.adapter = ConcatAdapter(bannerAdapter, artistsAdapter)
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

    private fun setupRecyclerViewAdapter(adapter: LibraryArtistsRecyclerViewAdapter) {
        adapter.onClickListener = LibraryArtistsRecyclerViewAdapter.OnClickListener { artist ->
            val intent = Intent(this, ArtistActivity::class.java)
            intent.putExtra(ArtistActivity.ARTIST_ANDROID_ID_LONG_KEY, artist.id.androidId)
            startActivity(intent)
        }

        adapter.onPlayLickedListener = LibraryArtistsRecyclerViewAdapter.OnPlayLickedListener { artist ->
            lifecycleScope.launch {
                val songs = artist.songs
                val playerSongs = LibraryToPlayerSongAdapter().execute(songs)
                val currentSongIndex = 0
                player?.playSongs(playerSongs, currentSongIndex)
            }
        }

        adapter.onAddToNextInQueueListener = LibraryArtistsRecyclerViewAdapter.OnAddToNextInQueueListener { artist ->
            val playerSongs = LibraryToPlayerSongAdapter().execute(artist.songs)
            player?.addAsNextToQueue(playerSongs)
        }

        adapter.onAddToEndInQueueListener = LibraryArtistsRecyclerViewAdapter.OnAddToEndInQueueListener { artist ->
            val playerSongs = LibraryToPlayerSongAdapter().execute(artist.songs)
            player?.addToEndOfQueue(playerSongs)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this@LibraryArtistsActivity)
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
