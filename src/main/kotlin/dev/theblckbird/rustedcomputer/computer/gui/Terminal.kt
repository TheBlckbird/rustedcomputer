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
    private var _stdout = ""
    val stdout get() = _stdout
    private var _stdin = ""
    val stdin get() = _stdin

    /**
     * Current character the cursor is at (zero-index)
     */
    private var _cursorChar = 0

    /**
     * Current character the cursor is at (zero-index)
     */
    val cursorChar get() = _cursorChar

    /**
     * Current line the cursor is at (zero-index)
     */
    private var _cursorLine = 0

    /**
     * Current character the cursor is at (zero-index)
     */
    val cursorLine get() = _cursorLine

    fun clearStdout() {
        _stdout = ""
    }

    fun clearStdin() {
        _stdin = ""
    }

    fun appendStdout(str: String) {
        _stdout += str
    }

    fun insertStdin(str: String) {
        _stdin = _stdin.replaceRange(_cursorChar, _cursorChar, str)

        for (i in 0..str.count()) {
            moveCursorRight()
        }
    }

    fun insertStdin(char: Char) {
        _stdin = _stdin.replaceRange(_cursorChar, _cursorChar, char.toString())
        moveCursorRight()
    }

    /**
     * Removes the last char at the current cursor position and moves the cursor back
     */
    fun backspaceStdin() {
        if (_cursorChar != 0) {
            _stdin = _stdin.removeRange(_cursorChar - 1, _cursorChar)
            moveCursorLeft()
        }
    }

    fun newlineStdin() {
        _stdin += '\n'
    }

    fun moveCursorLeft() {
        _cursorChar -= 1

        if (_cursorChar < 0) {
            _cursorChar = 0
        }

        /*if (_cursorChar < 0) {
            if (_cursorLine > 0) {
                _cursorLine -= 1
                _cursorChar = _stdin.lines()[_cursorLine].count()
            } else {
                _cursorChar += 1
            }
        }*/
    }

    fun moveCursorRight() {
        _cursorChar += 1

        if (_cursorChar > _stdin.count()) {
            _cursorChar -= 1
        }

        /*val stdinLines = _stdin.lines()

        if (_cursorChar > stdinLines[_cursorLine].count()) {
            if (_cursorLine == stdinLines.count()) {
                _cursorChar -= 1
            } else {
                _cursorChar = 0
                _cursorLine += 1
            }
        }*/
    }
}