package dev.ayer.dmusic.entity

enum class Theme(val id: Int) {
    ORANGE(0),
    GREEN(1),
    BLUE(2),
    PINK(3),
    PURPLE(4);

    companion object {
        private val default: Theme = ORANGE

        fun fromId(id: Int): Theme {
            return values().find { it.id == id } ?: default
        }
    }
}
