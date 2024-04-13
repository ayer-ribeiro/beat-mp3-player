package dev.ayer.medialibrary.entity.media

interface Media<T> {
    fun hasSameId(other: T): Boolean
}