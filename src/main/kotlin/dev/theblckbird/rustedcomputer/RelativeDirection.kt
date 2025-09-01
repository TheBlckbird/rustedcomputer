package dev.theblckbird.rustedcomputer

enum class RelativeDirection {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    FRONT,
    BACK;

    companion object {
        fun fromInt(integer: Int): RelativeDirection? {
            return when (integer) {
                0 -> TOP
                1 -> BOTTOM
                2 -> LEFT
                3 -> RIGHT
                4 -> FRONT
                5 -> BACK
                else -> null
            }
        }

        fun fromString(string: String): RelativeDirection? {
            return when(string.lowercase()) {
                "top" -> TOP
                "bottom" -> BOTTOM
                "left" -> LEFT
                "right" -> RIGHT
                "front" -> FRONT
                "back" -> BACK
                else -> null
            }
        }
    }

    fun toInt(): Int {
        return when (this) {
            TOP -> 0
            BOTTOM -> 1
            LEFT -> 2
            RIGHT -> 3
            FRONT -> 4
            BACK -> 5
        }
    }
}