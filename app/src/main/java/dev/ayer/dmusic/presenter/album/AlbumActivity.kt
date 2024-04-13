package dev.ayer.dmusic.presenter.album

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dev.ayer.audioplayer.transport.Player
import dev.ayer.audioplayer.transport.service.PlayerService
import dev.ayer.dmusic.R
import dev.ayer.dmusic.domain.LibraryToPlayerSongAdapter
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.transport.Library
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch

class AlbumActivity : CustomThemeBaseActivity() {

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
    private lateinit var albumThumbImageView: AppCompatImageView
    private lateinit var albumHeaderImageView: AppCompatImageView
    private lateinit var albumNameTextView: TextView
    private lateinit var albumInfoTextView: TextView
    private lateinit var toolbar: Toolbar

    private lateinit var adapter: AlbumSongsAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var album: LibraryAlbum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        albumThumbImageView = findViewById(R.id.album_thumb)
        albumHeaderImageView = findViewById(R.id.header_card_image)
        albumNameTextView = findViewById(R.id.album_name)
        albumInfoTextView = findViewById(R.id.album_info)
        toolbar = findViewById(R.id.toolbar)

        adapter = AlbumSongsAdapter()
        recycler = findViewById(R.id.recycler_view)

        val albumIdLong = intent.getLongExtra(ALBUM_ANDROID_ID_LONG_KEY, -1)
        val albumId = AlbumId.fromAndroidId(albumIdLong)
        album = library.getAlbumById(albumId)!!

        albumNameTextView.text = album.name
        if (album.releaseYear <= 0) {
            albumInfoTextView.text = album.artist.name
        } else {
            albumInfoTextView.text = "${album.artist.name} - ${album.releaseYear}"
        }

        Glide.with(this)
            .load(album.getAlbumArtUri())
            .transform(CenterCrop())
            .into(albumThumbImageView)

        Glide.with(this)
            .load(album.getAlbumArtUri())
            .transform(CenterCrop(), BlurTransformation(50))
            .into(albumHeaderImageView)


        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)
        setupRecyclerView(recycler)
        setupRecyclerViewAdapter(recycler, adapter)

        adapter.submitList(album.songs)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun setupRecyclerViewAdapter(recyclerView: RecyclerView, adapter: AlbumSongsAdapter) {
        adapter.onClickListener = AlbumSongsAdapter.OnClickListener { song ->
            playSong(song)
        }

        adapter.onPlayLickedListener = AlbumSongsAdapter.OnPlayLickedListener { song ->
            playSong(song)
        }

        adapter.onAddToNextInQueueListener = AlbumSongsAdapter.OnAddToNextInQueueListener { song ->
            val playerSong = LibraryToPlayerSongAdapter().execute(song)
            player?.addAsNextToQueue(listOf(playerSong))
        }

        adapter.onAddToEndInQueueListener = AlbumSongsAdapter.OnAddToEndInQueueListener { song ->
            val playerSong = LibraryToPlayerSongAdapter().execute(song)
            player?.addToEndOfQueue(listOf(playerSong))
        }
        recyclerView.adapter = adapter
    }

    private fun playSong(librarySong: LibrarySong) {
        lifecycleScope.launch {
            val songs = album.songs
            val playerSongs = LibraryToPlayerSongAdapter().execute(songs)
            val currentPlayerSong = playerSongs.find { it.id.androidId == librarySong.id.androidId }
            val currentSongIndex = playerSongs.indexOf(currentPlayerSong)
            player?.playSongs(playerSongs, currentSongIndex)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this@AlbumActivity)
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

            adapter.currentPlayingSong = currentSong
            adapter.notifyDataSetChanged()
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

    private fun LibraryAlbum.getAlbumArtUri(): Uri {
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, id.androidId)
    }

    companion object {
        const val ALBUM_ANDROID_ID_LONG_KEY = "artist_android_id_long_key"
    }
}
