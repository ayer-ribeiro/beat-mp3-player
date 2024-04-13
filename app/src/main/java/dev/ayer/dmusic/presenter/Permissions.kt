package dev.ayer.dmusic.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

val mediaPermission: String get() {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

fun Context.hasReadMediaPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        mediaPermission
    ) == PackageManager.PERMISSION_GRANTED
}

fun ActivityResultLauncher<String>.requestMediaPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.launch(
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        this.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

fun Activity.shouldShowRequestPermissionRationaleCompat(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && this.shouldShowRequestPermissionRationale(mediaPermission)
}
