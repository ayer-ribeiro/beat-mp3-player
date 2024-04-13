package dev.ayer.medialibrary.entity.media

import dev.ayer.medialibrary.entity.Duration
import dev.ayer.medialibrary.entity.keys.AlbumId
import dev.ayer.medialibrary.entity.keys.ArtistId
import dev.ayer.medialibrary.entity.keys.SongId
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(MockitoJUnitRunner::class)
class ArtistTest {

    private lateinit var artist: LibraryArtist

    @Before
    fun arrangeArtist() {
        artist = Mockito.mock(LibraryArtist::class.java)
    }

    @Test
    fun assertAlbumsWithAlphabeticalOrder() {
        // Given
        val albumsMock = arrangeAlbumsWithNames(listOf(
            "Chegou a Hora De Recomeçar",
            "21",
            "Save Your Tears (Remix)",
            "Dawn FM",
            "Patroas 35%",
            "Nosso Amor Envelheceu",
        ))

        val artist = LibraryArtist(
            Mockito.mock(ArtistId::class.java),
            "name",
            ArrayList(),
            albumsMock
        )

        // When
        val albumsWithAlphabeticalOrder = artist.albumsWithAlphabeticalOrder

        // Then
        assertSame(albumsMock.find { it.name == "21" },                         albumsWithAlphabeticalOrder[0])
        assertSame(albumsMock.find { it.name == "Chegou a Hora De Recomeçar" }, albumsWithAlphabeticalOrder[1])
        assertSame(albumsMock.find { it.name == "Dawn FM" },                    albumsWithAlphabeticalOrder[2])
        assertSame(albumsMock.find { it.name == "Nosso Amor Envelheceu" },      albumsWithAlphabeticalOrder[3])
        assertSame(albumsMock.find { it.name == "Patroas 35%" },                albumsWithAlphabeticalOrder[4])
        assertSame(albumsMock.find { it.name == "Save Your Tears (Remix)" },    albumsWithAlphabeticalOrder[5])
    }

    private fun arrangeAlbumsWithNames(names: List<String>): ArrayList<LibraryAlbum> {
        val albums = ArrayList<LibraryAlbum>()

        names.forEach { name ->
            val album = LibraryAlbum(
                Mockito.mock(AlbumId::class.java),
                name,
                Mockito.mock(LibraryArtist::class.java),
                0,
                ArrayList(),
            )

            albums.add(album)
        }

        return albums
    }

    @Test
    fun assertAlbumsWithReleaseYearOrder() {
        // Given
        val albumsMock = arrangeAlbumsWithReleaseYears(listOf(
            2020L,
            2015L,
            1975L,
            2022L,
            1993L,
            1999L,
            2007L,
        ))

        val artist = LibraryArtist(
            Mockito.mock(ArtistId::class.java),
            "name",
            ArrayList(),
            albumsMock
        )

        // When
        val albumsWithAlphabeticalOrder = artist.albumsWithReleaseYearOrder

        // Then
        assertSame(albumsMock.find { it.releaseYear == 1975L }, albumsWithAlphabeticalOrder[0])
        assertSame(albumsMock.find { it.releaseYear == 1993L }, albumsWithAlphabeticalOrder[1])
        assertSame(albumsMock.find { it.releaseYear == 1999L }, albumsWithAlphabeticalOrder[2])
        assertSame(albumsMock.find { it.releaseYear == 2007L }, albumsWithAlphabeticalOrder[3])
        assertSame(albumsMock.find { it.releaseYear == 2015L }, albumsWithAlphabeticalOrder[4])
        assertSame(albumsMock.find { it.releaseYear == 2020L }, albumsWithAlphabeticalOrder[5])
        assertSame(albumsMock.find { it.releaseYear == 2022L }, albumsWithAlphabeticalOrder[6])
    }

    private fun arrangeAlbumsWithReleaseYears(releaseYears: List<Long>): ArrayList<LibraryAlbum> {
        val albums = ArrayList<LibraryAlbum>()

        releaseYears.forEach { releaseYear ->
            val album = LibraryAlbum(
                Mockito.mock(AlbumId::class.java),
                "name",
                Mockito.mock(LibraryArtist::class.java),
                releaseYear,
                ArrayList(),
            )

            albums.add(album)
        }

        return albums
    }

    @Test
    fun assertSongsWithAlphabeticalOrder() {
        // Given
        val songsMock = arrangeSongsWithNames(listOf(
            "Chegou a Hora De Recomeçar",
            "21",
            "Save Your Tears (Remix)",
            "Dawn FM",
            "Patroas 35%",
            "Nosso Amor Envelheceu",
        ))

        val artist = LibraryArtist(
            Mockito.mock(ArtistId::class.java),
            "name",
            songsMock,
            ArrayList()
        )

        // When
        val songsWithAlphabeticalOrder = artist.songsWithAlphabeticalOrder

        // Then
        assertSame(songsMock.find { it.name == "21" },                         songsWithAlphabeticalOrder[0])
        assertSame(songsMock.find { it.name == "Chegou a Hora De Recomeçar" }, songsWithAlphabeticalOrder[1])
        assertSame(songsMock.find { it.name == "Dawn FM" },                    songsWithAlphabeticalOrder[2])
        assertSame(songsMock.find { it.name == "Nosso Amor Envelheceu" },      songsWithAlphabeticalOrder[3])
        assertSame(songsMock.find { it.name == "Patroas 35%" },                songsWithAlphabeticalOrder[4])
        assertSame(songsMock.find { it.name == "Save Your Tears (Remix)" },    songsWithAlphabeticalOrder[5])
    }

    private fun arrangeSongsWithNames(names: List<String>): ArrayList<LibrarySong> {
        val songs = ArrayList<LibrarySong>()

        names.forEach { name ->
            val song = LibrarySong(
                Mockito.mock(SongId::class.java),
                name,
                Mockito.mock(Date::class.java),
                Mockito.mock(Duration::class.java),
                Mockito.mock(LibraryArtist::class.java),
                Mockito.mock(LibraryAlbum::class.java)
            )

            songs.add(song)
        }

        return songs
    }
}
