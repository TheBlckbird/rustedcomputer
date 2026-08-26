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

    val visibleSegments: List<String>
        get() {
            var segments = wrappedSegments(stdout)

            // TODO: scrolling
            val scrolledLines = 0

            if (segments.count() > lines + 1) {
                segments = segments.slice(
                    segments.count() - 1 - lines - scrolledLines..<segments.count() - scrolledLines
                )
            }

            return segments.map {
                stdout.slice(it.start..<it.end)
            }
        }

    fun clearStdout() {
        stdout = ""
    }

    fun appendStdout(str: String) {
        stdout += str
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

    data class Segment(
        val start: Int,
        val end: Int,
    ) {
        val length: Int
            get() = end - start
    }
}