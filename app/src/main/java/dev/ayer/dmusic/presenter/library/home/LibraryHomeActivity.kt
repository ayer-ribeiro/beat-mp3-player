package dev.ayer.dmusic.presenter.library.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.Banner
import dev.ayer.dmusic.R
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.hasReadMediaPermission
import dev.ayer.dmusic.presenter.library.albums.LibraryAlbumsActivity
import dev.ayer.dmusic.presenter.library.artists.LibraryArtistsActivity
import dev.ayer.dmusic.presenter.library.songs.LibrarySongsActivity
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.dmusic.presenter.requestMediaPermission
import dev.ayer.dmusic.presenter.settings.SettingsActivity
import dev.ayer.dmusic.presenter.shouldShowRequestPermissionRationaleCompat
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.medialibrary.transport.Library
import kotlinx.coroutines.launch


class LibraryHomeActivity : CustomThemeBaseActivity() {

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
    private lateinit var songsItemView: View
    private lateinit var albumsItemView: View
    private lateinit var artistsItemView: View
    private lateinit var settingsItemView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library_home_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        songsItemView = findViewById(R.id.music_library_item_background)
        albumsItemView = findViewById(R.id.album_library_item_background)
        artistsItemView = findViewById(R.id.artist_library_item_background)
        settingsItemView = findViewById(R.id.settings_library_item_background)

        songsItemView.setOnClickListener {
            val intent = Intent(this, LibrarySongsActivity::class.java)
            startActivity(intent)
        }

        albumsItemView.setOnClickListener {
            val intent = Intent(this, LibraryAlbumsActivity::class.java)
            startActivity(intent)
        }

        artistsItemView.setOnClickListener {
            val intent = Intent(this, LibraryArtistsActivity::class.java)
            startActivity(intent)
        }

        settingsItemView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        miniPlayerView.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)

        val adLoader: AdLoader = AdLoader.Builder(this, Banner.HOME_TOP_NATIVE.adUnit)
            .forNativeAd { nativeAd ->
                val styles = NativeTemplateStyle.Builder().build()
                val template = findViewById<TemplateView>(R.id.banner)
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    val template = findViewById<TemplateView>(R.id.banner)
                    val placeholder = findViewById<View>(R.id.banner_placeholder)
                    template.visibility = View.VISIBLE
                    placeholder.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // exibir conteúdo
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
                // exibir conteúdo
            }
            shouldShowRequestPermissionRationaleCompat() -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
            }
            else -> {
                requestPermissionLauncher.requestMediaPermission()
            }
        }
    }

    private fun setupMiniPlayerSeekBar(miniPlayerView: MiniPlayerView) {
        miniPlayerView.maxSeekBar = 500
    }

    private fun setupMiniPlayerListeners(miniPlayerView: MiniPlayerView) {
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
