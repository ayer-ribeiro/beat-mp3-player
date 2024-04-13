package dev.ayer.medialibrary.data

interface AndroidDatabaseFetcher {
    fun getAndroidMediaSongs(): List<AndroidMediaSongData>
}
