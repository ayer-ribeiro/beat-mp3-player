package dev.ayer.dmusic.domain

import dev.ayer.dmusic.entity.Theme

fun interface UserThemeListener {
    fun onChanged(theme: Theme)
}