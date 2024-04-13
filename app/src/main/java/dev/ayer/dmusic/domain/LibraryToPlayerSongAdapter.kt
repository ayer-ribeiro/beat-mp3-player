package dev.ayer.dmusic.domain

import dev.ayer.audioplayer.entity.AndroidId
import dev.ayer.audioplayer.entity.PlayerTime
import dev.ayer.audioplayer.entity.media.PlayerAlbum
import dev.ayer.audioplayer.entity.media.PlayerArtist
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.medialibrary.entity.media.LibrarySong

class LibraryToPlayerSongAdapter {

    fun execute(librarySong: LibrarySong): PlayerSong {
        return PlayerSong(
            id = AndroidId.fromAndroidId(librarySong.id.androidId),
            name = librarySong.name,
            album = PlayerAlbum(
                androidId = librarySong.album.id.androidId,
                albumName = librarySong.album.name,
            ),
            artist = PlayerArtist(
                androidId = librarySong.artist.id.androidId,
                artistName = librarySong.artist.name
            ),
            duration = PlayerTime.fromMilliSeconds(librarySong.duration.toMilliseconds())
        )
    }

    fun execute(librarySongs: List<LibrarySong>): List<PlayerSong> {
        return librarySongs.map { librarySong ->
            execute(librarySong)
        }
    }
}
