package dev.theblckbird.rustedcomputer.computer.gui

class Terminal(
    /**
     * Width of the terminal in characters
     */
    val width: Int,
    /**
     * Height of the terminal in characters
     */
    val height: Int,
) {
    private var buffer = ""

    fun clear() {
        buffer = ""
    }

    fun append(str: String) {
        buffer += str
    }

    fun newline() {
        buffer += "\n"
    }

    fun getBuffer(): String {
        return buffer
    }
}