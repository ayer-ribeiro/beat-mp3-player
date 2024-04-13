package dev.ayer.audioplayer.entity

enum class RepeatMode(val id: Int) {
    NONE(0),
    REPEAT_ONE(1),
    REPEAT_ALL(2);

    companion object {
        fun fromId(id: Int): RepeatMode? {
            return values().find { it.id == id }
        }
    }
}
