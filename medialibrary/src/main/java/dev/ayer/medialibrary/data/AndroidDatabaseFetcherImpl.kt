package dev.ayer.medialibrary.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class AndroidDatabaseFetcherImpl(private val context: Context): AndroidDatabaseFetcher {

    override fun getAndroidMediaSongs(): List<AndroidMediaSongData> {
        val songs = ArrayList<AndroidMediaSongData>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATE_MODIFIED,
        )

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val contentResolver: ContentResolver = context.contentResolver

        val query = contentResolver.query(uri, projection, selection, null, sortOrder)

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val androidId = cursor.getLong(idColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    androidId
                )

                val songData = AndroidMediaSongData(
                    androidId = androidId,
                    uri = contentUri,
                    title = cursor.getString(titleColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    artistName = cursor.getString(artistColumn),
                    albumId = cursor.getLong(albumIdColumn),
                    albumName = cursor.getString(albumColumn),
                    track = cursor.getInt(trackColumn),
                    durationMs = cursor.getLong(durationColumn),
                    dateModified = cursor.getLong(dateModifiedColumn),
                    year = cursor.getLong(yearColumn)
                )
                songs.add(songData)
            }
        }
        return songs
    }
}
