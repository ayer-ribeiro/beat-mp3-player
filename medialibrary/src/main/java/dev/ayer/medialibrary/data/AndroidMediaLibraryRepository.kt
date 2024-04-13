package dev.ayer.medialibrary.data

import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.domain.repository.MediaLibraryRepository
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.keys.SongId

internal class AndroidMediaLibraryRepository(
    private val androidDatabaseFetcher: AndroidDatabaseFetcher,
): MediaLibraryRepository {

    private val androidMediaDataAdapter = AndroidMediaDataAdapter()
    private var mediaDataAdapterData = androidMediaDataAdapter.getDataFromAndroidMediaSongDataList(
        androidDatabaseFetcher.getAndroidMediaSongs()
    )

    override fun getSongs(): List<LibrarySong> {
        return mediaDataAdapterData.songs
    }

    override fun getArtists(): List<LibraryArtist> {
        return mediaDataAdapterData.artists
    }

    override fun getAlbums(): List<LibraryAlbum> {
        return mediaDataAdapterData.albums
    }

    override fun getSongsHashMap(): HashMap<SongId, LibrarySong> {
        return mediaDataAdapterData.allSongsHashMap
    }

    override fun getArtistsHashMap(): HashMap<ArtistId, LibraryArtist> {
        return mediaDataAdapterData.allArtistsHashMap
    }

    override fun getAlbumsHashMap(): HashMap<AlbumId, LibraryAlbum> {
        return mediaDataAdapterData.allAlbumsHashMap
    }

    override fun refresh() {
        mediaDataAdapterData = androidMediaDataAdapter.getDataFromAndroidMediaSongDataList(
            androidDatabaseFetcher.getAndroidMediaSongs()
        )
    }
}
