package dev.ayer.medialibrary.mock

import android.net.Uri
import dev.ayer.medialibrary.data.AndroidMediaSongData
import org.mockito.Mockito

class SongDataRepositoryMock {

    fun getMockSongs(): List<AndroidMediaSongData> {

        val previewSong = AndroidMediaSongData(
            androidId = 1,
            uri = Mockito.mock(Uri::class.java),
            title = "Preview",
            artistId = 123,
            artistName = "Adele",
            albumId = 321,
            albumName = "21",
            track = 0,
            dateModified = 555,
            durationMs = 50000,
            year = 2015
        )

        val helloSong = AndroidMediaSongData(
            androidId = 2,
            uri = Mockito.mock(Uri::class.java),
            title = "Hello",
            artistId = 123,
            artistName = "Adele",
            albumId = 321,
            albumName = "21",
            track = 1,
            dateModified = 556,
            durationMs = 180000,
            year = 2015
        )

//        val unknownSong = SongData(
//            androidId = 3,
//            uri = Mockito.mock(Uri::class.java),
//            title =  null,
//            artistId = null,
//            artistName = null,
//            albumId = 441,
//            albumName = "Adele",
//            track = 1,
//            dateModified = 556,
//            durationMs = 170000,
//            year = null
//        )

        val someoneLikeYouSong = AndroidMediaSongData(
            androidId = 4,
            uri = Mockito.mock(Uri::class.java),
            title = "Someone Like you",
            artistId = 123,
            artistName = "Adele",
            albumId = 321,
            albumName = "21",
            track = 2,
            dateModified = 557,
            durationMs = 200000,
            year = 2015
        )

        val diasAtrasSong = AndroidMediaSongData(
            androidId = 5,
            uri = Mockito.mock(Uri::class.java),
            title = "Dias Atrás",
            artistId = 2,
            artistName = "CPM 22",
            albumId = 2,
            albumName = "Chegou a hora de recomeçar",
            track = 5,
            dateModified = 558,
            durationMs = 120000,
            year = 2003
        )

        return listOf(
            helloSong,
//            unknownSong,
            someoneLikeYouSong,
            diasAtrasSong,
            previewSong
        )
    }
}
