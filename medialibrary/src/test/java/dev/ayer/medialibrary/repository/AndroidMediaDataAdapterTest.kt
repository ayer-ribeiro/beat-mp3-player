package dev.ayer.medialibrary.repository

import dev.ayer.medialibrary.data.AndroidMediaDataAdapter
import dev.ayer.medialibrary.entity.media.LibrarySong
import dev.ayer.medialibrary.mock.SongDataRepositoryMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class AndroidMediaDataAdapterTest {
    private lateinit var secondSongFromAct: LibrarySong

    // region Before (arrangeAndActBeforeAssert)
    @Before
    fun arrangeAndActBeforeAssert() {
        //Arrange
        val repositorySongs = SongDataRepositoryMock().getMockSongs()
        val androidMediaDataAdapter = AndroidMediaDataAdapter()

        // Act
        val songsFromMediaDataAdapterData = androidMediaDataAdapter.getDataFromAndroidMediaSongDataList(repositorySongs)
        val songsFromMediaDataAdapter = songsFromMediaDataAdapterData.songs
        secondSongFromAct = songsFromMediaDataAdapter.find { it.name == "Hello" }!!
    }

    // endregion

    // region Song base fields tests

    @Test
    fun assertSongName() {
        Assert.assertEquals("Hello", secondSongFromAct.name)
    }

    @Test
    fun assertSongAndroidId() {
        Assert.assertEquals(2, secondSongFromAct.id.androidId)
    }

    @Test
    fun assertSongDuration() {
        Assert.assertEquals(180L, secondSongFromAct.duration.toSeconds())
    }

    @Test
    fun assertSongLastModified() {
        Assert.assertEquals(Date(556), secondSongFromAct.lastModified)
    }

    // endregion

    // region Song artist fields tests

    @Test
    fun assertSongArtistName() {
        Assert.assertEquals("Adele", secondSongFromAct.artist.name)
    }

    @Test
    fun assertSongArtistId() {
        Assert.assertEquals(123, secondSongFromAct.artist.id.androidId)
    }

    @Test
    fun assertSongArtistSongsSize() {
        Assert.assertEquals(3, secondSongFromAct.artist.songs.size)
    }

    @Test
    fun assertArtistSongsContainsSong() {
        Assert.assertEquals(true, secondSongFromAct.artist.songs.any { it === secondSongFromAct })
    }

    @Test
    fun assertArtistAlbumsContainsSongAlbum() {
        Assert.assertEquals(
            true,
            secondSongFromAct.artist.albums.any { it === secondSongFromAct.album })
    }

    @Test
    fun assertSongArtistAlbumsSize() {
        Assert.assertEquals(1, secondSongFromAct.artist.albums.size)
    }

    // endregion

    // region Song album fields tests

    @Test
    fun assertAlbumId() {
        Assert.assertEquals("321", secondSongFromAct.album.id.androidId)
    }

    @Test
    fun assertAlbumName() {
        Assert.assertSame("21", secondSongFromAct.album.name)
    }

    @Test
    fun assertAlbumReleaseYear() {
        Assert.assertEquals(2015, secondSongFromAct.album.releaseYear)
    }

    @Test
    fun assertSameArtistThanSong() {
        Assert.assertSame(secondSongFromAct.artist, secondSongFromAct.album.artist)
    }

    @Test
    fun assertSongAlbumSongsSize() {
        Assert.assertEquals(3, secondSongFromAct.album.songs.size)
    }

    @Test
    fun assertAlbumSongsContainsSong() {
        Assert.assertEquals(true, secondSongFromAct.album.songs.any { it === secondSongFromAct })
    }

    // endregion
}