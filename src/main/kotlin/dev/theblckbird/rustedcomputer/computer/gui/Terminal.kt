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
        cursorIndex = displayPrefix().length
        syncCursorFromIndex()
    }

    fun clearStdout() {
        stdout = ""
        cursorIndex = cursorIndex.coerceAtLeast(displayPrefix().length)
        syncCursorFromIndex()
    }

    fun clearStdin() {
        stdin = ""
        cursorIndex = displayPrefix().length
        preferredColumn = 0
        syncCursorFromIndex()
    }

    fun appendStdout(str: String) {
        val wasAtEnd = cursorIndex == fullInput().length

        stdout += str

        if (wasAtEnd) {
            cursorIndex = fullInput().length
        } else {
            cursorIndex = cursorIndex.coerceAtLeast(displayPrefix().length)
        }

        syncCursorFromIndex()
        preferredColumn = cursorChar
    }

    fun insertStdin(str: String) {
        val localIndex = (cursorIndex - displayPrefix().length).coerceIn(0, stdin.length)
        stdin = stdin.replaceRange(localIndex, localIndex, str)
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
        val prefixLength = displayPrefix().length
        if (cursorIndex <= prefixLength) return

        val localIndex = (cursorIndex - prefixLength).coerceIn(0, stdin.length)
        stdin = stdin.removeRange(localIndex - 1, localIndex)
        cursorIndex -= 1
        syncCursorFromIndex()
        preferredColumn = cursorChar
    }

    fun newlineStdin() {
        insertStdin('\n')
    }

    fun moveCursorLeft() {
        val minIndex = displayPrefix().length
        if (cursorIndex > minIndex) {
            cursorIndex -= 1
            syncCursorFromIndex()
            preferredColumn = cursorChar
        }
    }

    fun moveCursorRight() {
        if (cursorIndex < fullInput().length) {
            cursorIndex += 1
            syncCursorFromIndex()
            preferredColumn = cursorChar
        }
    }

    fun moveCursorUp() {
        val segments = wrappedSegments(fullInput())
        if (segments.isEmpty()) return

        val currentSegmentIndex = segmentIndexAtCursor(segments)
        if (currentSegmentIndex <= 0) return

        val target = segments[currentSegmentIndex - 1]
        cursorIndex = (target.start + minOf(preferredColumn, target.length)).coerceIn(target.start, target.end)
        syncCursorFromIndex()
    }

    fun moveCursorDown() {
        val segments = wrappedSegments(fullInput())
        if (segments.isEmpty()) return

        val currentSegmentIndex = segmentIndexAtCursor(segments)
        if (currentSegmentIndex >= segments.lastIndex) return

        val target = segments[currentSegmentIndex + 1]
        cursorIndex = (target.start + minOf(preferredColumn, target.length)).coerceIn(target.start, target.end)
        syncCursorFromIndex()
    }

    private fun syncCursorFromIndex() {
        cursorIndex = cursorIndex.coerceIn(displayPrefix().length, fullInput().length)

        val segments = wrappedSegments(fullInput())
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

        cursorLine += wrappedSegments(stdout).count() - 1

        if (stdout.endsWith('\n')) {
            cursorLine -= 1
        }
    }

    private fun displayPrefix(): String {
        if (stdout.isEmpty()) return ""

        val endsWithNewline = stdout.endsWith("\n") || stdout.endsWith("\r\n") || stdout.endsWith("\r")
        if (endsWithNewline) return ""

        return stdout.lines().lastOrNull().orEmpty()
    }

    private fun fullInput(): String = displayPrefix() + stdin

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
    private fun wrappedSegments(text: String): List<Segment> {
        val width = characters.coerceAtLeast(1)
        val result = mutableListOf<Segment>()

        var lineStart = 0
        var column = 0
        var i = 0

        if (text.isEmpty()) {
            result += Segment(0, 0)
            return result
        }

        while (i < text.length) {
            val char = text[i]

            if (char == '\n') {
                result += Segment(lineStart, i)
                lineStart = i + 1
                column = 0
                i += 1

                if (i == text.length) {
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

        if (lineStart <= text.length) {
            result += Segment(lineStart, text.length)
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