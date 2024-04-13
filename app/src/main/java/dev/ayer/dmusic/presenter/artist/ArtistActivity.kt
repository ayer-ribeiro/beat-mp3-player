package dev.ayer.dmusic.presenter.artist

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
import dev.ayer.dmusic.presenter.album.AlbumSongsAdapter
import dev.ayer.dmusic.presenter.customviews.MiniPlayerView
import dev.ayer.dmusic.presenter.player.PlayerActivity
import dev.ayer.dmusic.presenter.theme.CustomThemeBaseActivity
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.transport.Library
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch

class ArtistActivity : CustomThemeBaseActivity() {

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
    private lateinit var headerImage: AppCompatImageView
    private lateinit var artistNameTextView: TextView
    private lateinit var toolbar: Toolbar

    private lateinit var adapter: ArtistSongsAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var artist: LibraryArtist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artist_activity)

        val playerIntent = Intent(this, PlayerService::class.java)
        startService(playerIntent)
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        miniPlayerView = findViewById(R.id.mini_player)
        miniPlayerCardView = findViewById(R.id.mini_player_card)
        headerImage = findViewById(R.id.header_image)
        artistNameTextView = findViewById(R.id.artist_name)
        toolbar = findViewById(R.id.toolbar)

        adapter = ArtistSongsAdapter()
        recycler = findViewById(R.id.recycler_view)


        val artistIdLong = intent.getLongExtra(ARTIST_ANDROID_ID_LONG_KEY, -1)
        val artistId = ArtistId.fromAndroidId(artistIdLong)

        artist = library.getArtistById(artistId)!!
        artistNameTextView.text = artist.name ?: getString(R.string.unknown_artist)

        Glide.with(this)
            .load(artist.getAlbumArtUri())
            .transform(CenterCrop(), BlurTransformation(50))
            .into(headerImage)

        setupMiniPlayerSeekBar(miniPlayerView)
        setupMiniPlayerListeners(miniPlayerView)
        setupRecyclerView(recycler)
        setupRecyclerViewAdapter(recycler, adapter)

       val songsGroupedByAlbum = artist.albumsWithReleaseYearOrder.fold(ArrayList<LibrarySong>(), { acc, album ->
            acc.addAll(album.songs)
            return@fold acc
        })
        adapter.submitList(songsGroupedByAlbum)

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

    private fun setupRecyclerViewAdapter(recyclerView: RecyclerView, adapter: ArtistSongsAdapter) {
        adapter.onClickListener = ArtistSongsAdapter.OnClickListener { song ->
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
            val songs = adapter.currentList
            val playerSongs = LibraryToPlayerSongAdapter().execute(songs)
            val currentPlayerSong = playerSongs.find { it.id.androidId == librarySong.id.androidId }
            val currentSongIndex = playerSongs.indexOf(currentPlayerSong)
            player?.playSongs(playerSongs, currentSongIndex)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this@ArtistActivity)
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

    private fun LibraryArtist.getAlbumArtUri(): Uri? {
        val album = this.albums.firstOrNull() ?: return null
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, album.id.androidId)
    }

    companion object {
        const val ARTIST_ANDROID_ID_LONG_KEY = "artist_android_id_long_key"
    }
}
