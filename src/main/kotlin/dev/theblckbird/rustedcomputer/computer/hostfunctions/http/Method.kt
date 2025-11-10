package dev.theblckbird.rustedcomputer.computer.hostfunctions.http

enum class Method {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;

    companion object {
        fun fromInt(number: Int): Method? {
            return when (number) {
                0 -> GET
                1 -> HEAD
                2 -> POST
                3 -> PUT
                4 -> DELETE
                5 -> CONNECT
                6 -> OPTIONS
                7 -> TRACE
                8 -> PATCH
                else -> null
            }
        }
    }
}

// TODO: add script to automatically generate enums
