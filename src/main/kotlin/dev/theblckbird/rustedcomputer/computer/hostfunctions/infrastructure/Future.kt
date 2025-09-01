package dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure

sealed class Future {
    object Pending : Future()
    object Failed : Future()

    data class Success(
        val content: String
    ) : Future() {
        override fun toString(): String = "S$content"
    }

    override fun toString(): String {
        return when(this) {
            Failed -> "F"
            Pending -> "P"
            is Success -> "S" // handled by the data class itself
        }
    }
}