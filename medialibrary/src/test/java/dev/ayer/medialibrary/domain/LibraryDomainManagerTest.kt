package dev.ayer.medialibrary.domain

import dev.ayer.medialibrary.data.AndroidDatabaseFetcher
import dev.ayer.medialibrary.data.AndroidMediaLibraryRepository
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.mock.SongDataRepositoryMock
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

class LibraryDomainManagerTest {

    @RunWith(MockitoJUnitRunner::class)
    class GetAllSongsWithAlphabeticalOrderTest {
        private lateinit var useCase: LibraryCacheManager
        private lateinit var songsWithAlphabeticalOrder: List<LibrarySong>
        private lateinit var adeleHelloSong: LibrarySong

        // region Before (arrangeAndActBeforeAssert)
        @Before
        fun arrangeAndActBeforeAssert() {
            //Arrange
            val repositorySongs = SongDataRepositoryMock().getMockSongs()
            val androidDatabaseFetcher = Mockito.mock(AndroidDatabaseFetcher::class.java)
            Mockito.`when`(androidDatabaseFetcher.getAndroidMediaSongs()).thenReturn(repositorySongs)

            val androidMediaLibraryRepository = AndroidMediaLibraryRepository(androidDatabaseFetcher)
            useCase = LibraryCacheManager(androidMediaLibraryRepository)

            // Act
            songsWithAlphabeticalOrder = useCase.getSongsWithAlphabeticalOrder()
            adeleHelloSong = songsWithAlphabeticalOrder.find { it.name == "Hello" }!!
        }

        // endregion

        // region Song base fields tests

        @Test
        fun assertSongName() {
            assertEquals("Hello", adeleHelloSong.name)
        }

        @Test
        fun assertSongId() {
            assertEquals(2, adeleHelloSong.id.androidId)
        }

        @Test
        fun assertSongDuration() {
            assertEquals(180L, adeleHelloSong.duration.toSeconds())
        }

        @Test
        fun assertSongLastModified() {
            assertEquals(Date(556), adeleHelloSong.lastModified)
        }

        // endregion

        // region Song artist fields tests

        @Test
        fun assertSongArtistName() {
            assertEquals("Adele", adeleHelloSong.artist.name)
        }

        @Test
        fun assertSongArtistId() {
            assertEquals(123, adeleHelloSong.artist.id.androidId)
        }

        @Test
        fun assertSongArtistSongsSize() {
            assertEquals(3, adeleHelloSong.artist.songs.size)
        }

        @Test
        fun assertArtistSongsContainsSong() {
            assertEquals(true, adeleHelloSong.artist.songs.any { it === adeleHelloSong })
        }

        @Test
        fun assertArtistAlbumsContainsSongAlbum() {
            assertEquals(
                true,
                adeleHelloSong.artist.albums.any { it === adeleHelloSong.album })
        }

        @Test
        fun assertSongArtistAlbumsSize() {
            assertEquals(1, adeleHelloSong.artist.albums.size)
        }

        // endregion

        // region Song album fields tests

        @Test
        fun assertAlbumId() {
            assertEquals(321, adeleHelloSong.album.id.androidId)
        }

        @Test
        fun assertAlbumName() {
            assertSame(21, adeleHelloSong.album.name)
        }

        @Test
        fun assertAlbumReleaseYear() {
            assertEquals(2015, adeleHelloSong.album.releaseYear)
        }

        @Test
        fun assertSameArtistThanSong() {
            assertSame(adeleHelloSong.artist, adeleHelloSong.album.artist)
        }

        @Test
        fun assertSongAlbumSongsSize() {
            assertEquals(3, adeleHelloSong.album.songs.size)
        }

        @Test
        fun assertAlbumSongsContainsSong() {
            assertEquals(true, adeleHelloSong.album.songs.any { it === adeleHelloSong })
        }

        // endregion
    }
}