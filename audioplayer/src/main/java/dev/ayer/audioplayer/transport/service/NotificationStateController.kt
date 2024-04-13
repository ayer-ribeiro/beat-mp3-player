package dev.ayer.audioplayer.transport.service

import android.content.ContentUris
import android.media.MediaMetadata
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LifecycleService
import dev.ayer.audioplayer.entity.media.PlayerSong
import dev.ayer.audioplayer.transport.Player

internal class NotificationStateController(service: LifecycleService, mediaSession: MediaSessionCompat, player: Player) {
    init {

        player.observables.currentSong.observe(service) { song ->
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, song?.name)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, song?.artist?.artistName)
                    .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, song?.getAlbumArtUri().toString())
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, song?.duration?.timeMillis ?: 0)
                    .build()
            )
        }

        player.observables.isPlaying.observe(service) { isPlaying ->
            val playbackState = if (isPlaying) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            }

            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        playbackState,
                        player.observables.currentTimeMillis.value ?: 0,
                        1.0f
                    )
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
            )
        }

        player.observables.currentTimeMillis.observe(service) { currentTimeMillis ->
            val playbackState = if (player.observables.isPlaying.value == true) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            }

            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        playbackState,
                        currentTimeMillis,
                        1.0f
                    )
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
            )
        }
    }

    private fun PlayerSong.getAlbumArtUri(): Uri? {
        val album = this.album ?: return null
        val songCover = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(songCover, album.androidId)
    }
}
