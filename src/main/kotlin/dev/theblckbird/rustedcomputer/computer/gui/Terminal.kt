package dev.theblckbird.rustedcomputer.computer.gui

class Terminal(
    /**
     * Width of the terminal in characters
     */
    val characters: Int,
    /**
     * Height of the terminal in characters
     */
    val lines: Int,
) {
    private var stdout = ""
    private var stdin = ""

    fun clearStdout() {
        stdout = ""
    }

    fun clearStdin() {
        stdin = ""
    }

    fun appendStdout(str: String) {
        stdout += str
    }

    fun appendStdout(char: Char) {
        stdout += char
    }

    fun newlineStdout() {
        stdout += '\n'
    }

    fun appendStdin(str: String) {
        stdin += str
    }

    fun appendStdin(char: Char) {
        stdin += char
    }

    fun backspaceStdin() {
        stdin = stdin.dropLast(1)
    }

    fun newlineStdin() {
        stdin += '\n'
    }

    fun getStdout(): String {
        return stdout
    }

    fun getStdin(): String {
        return stdin
    }
}