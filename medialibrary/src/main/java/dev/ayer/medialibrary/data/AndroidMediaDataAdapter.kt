package dev.ayer.medialibrary.data

import dev.ayer.medialibrary.entity.Duration
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.keys.SongId
import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class AndroidMediaDataAdapter {

    internal class AndroidMediaDataAdapterData(
        val songs: ArrayList<LibrarySong>,
        val artists: ArrayList<LibraryArtist>,
        val albums: ArrayList<LibraryAlbum>,
        val allSongsHashMap: HashMap<SongId, LibrarySong>,
        val allAlbumsHashMap: HashMap<AlbumId, LibraryAlbum>,
        val allArtistsHashMap: HashMap<ArtistId, LibraryArtist>,
    )

    fun getDataFromAndroidMediaSongDataList(
        repositorySongs: List<AndroidMediaSongData>
    ): AndroidMediaDataAdapterData {
        val allSongsHashMap: HashMap<SongId, LibrarySong> = HashMap()
        val allAlbumsHashMap: HashMap<AlbumId, LibraryAlbum> = HashMap()
        val allArtistsHashMap: HashMap<ArtistId, LibraryArtist> = HashMap()
        val songs: ArrayList<LibrarySong> = ArrayList()
        val artists: ArrayList<LibraryArtist> = ArrayList()
        val albums: ArrayList<LibraryAlbum> = ArrayList()

        repositorySongs.forEach { songData ->
            val artistId = songData.extractArtistId()
            val cachedArtist = allArtistsHashMap[artistId]

            val albumId = songData.extractAlbumId()
            val album = allAlbumsHashMap[albumId] ?: songData.extractAlbumBaseData(cachedArtist)
            val artist = album.artist

            val songId = SongId.createWithAndroidIdAndUri(
                androidId = songData.androidId,
                uri = songData.uri
            )
            val song = LibrarySong(
                id = songId,
                name = songData.title,
                duration = Duration.fromMilliseconds(songData.durationMs),
                artist = artist,
                album = album,
                lastModified = Date(songData.dateModified)
            )

            allSongsHashMap[song.id] = song
            allAlbumsHashMap[album.id] = album
            allArtistsHashMap[artist.id] = artist

            albums.addAlbumIfNotContains(album)
            artists.addArtistIfNotContains(artist)
            songs.addSongIfNotContains(song)

            album.addSong(song)
            artist.addSong(song)
            artist.addAlbum(album)
        }

        return AndroidMediaDataAdapterData(
            songs,
            artists,
            albums,
            allSongsHashMap,
            allAlbumsHashMap,
            allArtistsHashMap
        )
    }

    private fun ArrayList<LibrarySong>.addSongIfNotContains(song: LibrarySong) {
        val alreadyContains = this.any { it.hasSameId(song) }
        if (!alreadyContains) {
            this.add(song)
        }
    }

    private fun ArrayList<LibraryArtist>.addArtistIfNotContains(artist: LibraryArtist) {
        val alreadyContains = this.any { it.hasSameId(artist) }
        if (!alreadyContains) {
            this.add(artist)
        }
    }

    private fun ArrayList<LibraryAlbum>.addAlbumIfNotContains(album: LibraryAlbum) {
        val alreadyContains = this.any { it.hasSameId(album) }
        if (!alreadyContains) {
            this.add(album)
        }
    }

    private fun AndroidMediaSongData.extractArtistId(): ArtistId {
        return ArtistId.fromAndroidId(this.artistId)
    }

    private fun AndroidMediaSongData.extractAlbumId(): AlbumId {
        return AlbumId.fromAndroidId(this.albumId)
    }

    private fun AndroidMediaSongData.extractAlbumBaseData(artist: LibraryArtist?): LibraryAlbum {
        val albumArtist = artist ?: LibraryArtist(
            this.extractArtistId(),
            this.artistName,
            ArrayList(),
            ArrayList(),
        )

        return LibraryAlbum(
            id = this.extractAlbumId(),
            name = this.albumName,
            artist = albumArtist,
            _songs = ArrayList(),
            releaseYear = this.year
        )
    }
}
