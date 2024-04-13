package dev.ayer.medialibrary.domain

import dev.ayer.medialibrary.entity.media.LibraryAlbum
import dev.ayer.medialibrary.entity.media.LibraryArtist
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.domain.repository.MediaLibraryRepository
import dev.ayer.medialibrary.domain.usecases.AlbumsWithAlphabeticalOrderUseCase
import dev.ayer.medialibrary.domain.usecases.ArtistsWithAlphabeticalOrderUseCase
import dev.ayer.medialibrary.domain.usecases.ShouldRefreshLibraryUseCase
import dev.ayer.medialibrary.domain.usecases.SongsWithAlphabeticalOrderUseCase
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import java.util.*
import kotlin.collections.HashMap

internal class LibraryCacheManager(
    private val repository: MediaLibraryRepository
) {
    private var lastUpdate: Date = Date()

    private var cachedSongsWithAlphabetical = SongsWithAlphabeticalOrderUseCase(repository.getSongs()).songsWithAlphabetical
    private var cachedArtistsWithAlphabetical = ArtistsWithAlphabeticalOrderUseCase(repository.getArtists()).artistsWithAlphabeticalOrder
    private var cachedAlbumsWithAlphabetical = AlbumsWithAlphabeticalOrderUseCase(repository.getAlbums()).albumsWithAlphabeticalOrder

    fun getSongsWithAlphabeticalOrder(): List<LibrarySong> {
        updateIfShouldRefresh()
        return cachedSongsWithAlphabetical
    }

    fun getArtistsWithAlphabeticalOrder(): List<LibraryArtist> {
        updateIfShouldRefresh()
        return cachedArtistsWithAlphabetical
    }

    fun getAlbumsWithAlphabeticalOrder(): List<LibraryAlbum> {
        updateIfShouldRefresh()
        return cachedAlbumsWithAlphabetical
    }

    fun getArtistsHashMap(): HashMap<ArtistId, LibraryArtist> {
        updateIfShouldRefresh()
        return repository.getArtistsHashMap()
    }

    fun getAlbumsHashMap(): HashMap<AlbumId, LibraryAlbum> {
        updateIfShouldRefresh()
        return repository.getAlbumsHashMap()
    }

    private fun updateIfShouldRefresh() {
        if (ShouldRefreshLibraryUseCase(lastUpdate).shouldRefresh) {
            repository.refresh()
            cachedSongsWithAlphabetical = SongsWithAlphabeticalOrderUseCase(repository.getSongs()).songsWithAlphabetical
            cachedArtistsWithAlphabetical = ArtistsWithAlphabeticalOrderUseCase(repository.getArtists()).artistsWithAlphabeticalOrder
            cachedAlbumsWithAlphabetical = AlbumsWithAlphabeticalOrderUseCase(repository.getAlbums()).albumsWithAlphabeticalOrder
            lastUpdate = Date()
        }
    }
}
