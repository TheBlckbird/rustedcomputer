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
    var stdout = ""
        private set

    var stdin = ""
        private set

    private var cursorIndex = 0
    private var preferredColumn = 0

    /**
     * Current character in the wrapped line the cursor is at (zero-index)
     */
    var cursorChar = 0
        private set

    /**
     * Current wrapped line the cursor is at (zero-index)
     */
    var cursorLine = 0
        private set

    init {
        syncCursorFromIndex()
    }

    fun clearStdout() {
        stdout = ""
    }

    fun clearStdin() {
        stdin = ""
        cursorIndex = 0
        preferredColumn = 0
        syncCursorFromIndex()
    }

    fun appendStdout(str: String) {
        stdout += str
    }

    fun insertStdin(str: String) {
        stdin = stdin.replaceRange(cursorIndex, cursorIndex, str)
        cursorIndex += str.length
        syncCursorFromIndex()
        preferredColumn = cursorChar
    }

    fun insertStdin(char: Char) {
        insertStdin(char.toString())
    }

    /**
     * Removes the char before the current cursor position and moves the cursor back
     */
    fun backspaceStdin() {
        if (cursorIndex == 0) return

        stdin = stdin.removeRange(cursorIndex - 1, cursorIndex)
        cursorIndex -= 1
        syncCursorFromIndex()
        preferredColumn = cursorChar
    }

    fun newlineStdin() {
        insertStdin('\n')
    }

    fun moveCursorLeft() {
        if (cursorIndex > 0) {
            cursorIndex -= 1
            syncCursorFromIndex()
            preferredColumn = cursorChar
        }
    }

    fun moveCursorRight() {
        if (cursorIndex < stdin.length) {
            cursorIndex += 1
            syncCursorFromIndex()
            preferredColumn = cursorChar
        }
    }

    fun moveCursorUp() {
        val segments = wrappedSegments()
        if (segments.isEmpty()) return

        val currentSegmentIndex = segmentIndexAtCursor(segments)
        if (currentSegmentIndex <= 0) return

        val target = segments[currentSegmentIndex - 1]
        cursorIndex = (target.start + minOf(preferredColumn, target.length)).coerceIn(target.start, target.end)
        syncCursorFromIndex()
    }

    fun moveCursorDown() {
        val segments = wrappedSegments()
        if (segments.isEmpty()) return

        val currentSegmentIndex = segmentIndexAtCursor(segments)
        if (currentSegmentIndex >= segments.lastIndex) return

        val target = segments[currentSegmentIndex + 1]
        cursorIndex = (target.start + minOf(preferredColumn, target.length)).coerceIn(target.start, target.end)
        syncCursorFromIndex()
    }

    private fun syncCursorFromIndex() {
        cursorIndex = cursorIndex.coerceIn(0, stdin.length)

        val segments = wrappedSegments()
        if (segments.isEmpty()) {
            cursorLine = 0
            cursorChar = 0
            return
        }

        val segmentIndex = segmentIndexAtCursor(segments)
        val segment = segments[segmentIndex]

        cursorLine = segmentIndex
        cursorChar = (cursorIndex - segment.start).coerceIn(0, segment.length)

        if (cursorChar == characters) {
            cursorLine += 1
            cursorChar = 0
        }
    }

    private fun segmentIndexAtCursor(segments: List<Segment>): Int {
        for ((index, segment) in segments.withIndex()) {
            if (cursorIndex in segment.start..segment.end) {
                return index
            }
        }

        return segments.lastIndex
    }

    /**
     * Get the stdin as a list of segments with line wrapping enabled
     */
    private fun wrappedSegments(): List<Segment> {
        val width = characters.coerceAtLeast(1)
        val result = mutableListOf<Segment>()

        var lineStart = 0
        var column = 0
        var i = 0

        if (stdin.isEmpty()) {
            result += Segment(0, 0)
            return result
        }

        while (i < stdin.length) {
            val char = stdin[i]

            if (char == '\n') {
                result += Segment(lineStart, i)
                lineStart = i + 1
                column = 0
                i += 1

                if (i == stdin.length) {
                    result += Segment(i, i)
                }
                continue
            }

            if (column == width) {
                result += Segment(lineStart, i)
                lineStart = i
                column = 0

                continue
            }

            column += 1
            i += 1
        }

        if (lineStart <= stdin.length) {
            result += Segment(lineStart, stdin.length)
        }

        return result
    }

    private data class Segment(
        val start: Int,
        val end: Int,
    ) {
        val length: Int
            get() = end - start
    }
}