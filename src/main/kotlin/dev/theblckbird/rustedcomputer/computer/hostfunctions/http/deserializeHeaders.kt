package dev.theblckbird.rustedcomputer.computer.hostfunctions.http

import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.RustedComputer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Deserializes the headers as specified in the documentation.
 *
 * It returns an array of strings, because that's the way, the HTTP client expects it:
 * The header name, then the header value repeating.
 */
fun deserializeHeaders(rawHeaders: ByteArray, memory: Memory): Array<String> {
    val nextInt = ByteArray(4)
    val headersAddressesLengths = mutableListOf<Int>()

    val headersDeserialized = Array(rawHeaders.count() / 4 / 2) { "" }

    for ((index, byte) in rawHeaders.withIndex()) {
        val nextIntIndex = index % 4

        nextInt[nextIntIndex] = byte

        if (nextIntIndex == 3) {
            headersAddressesLengths.add(byteArrayToInt(nextInt))
        }
    }

    var nextAddress: Int? = null
    var nextLength: Int? = null
    var nextIndex = 0

    for ((index, header) in headersAddressesLengths.withIndex()) {
        if (index % 2 == 0) {
            nextAddress = header
        } else {
            nextLength = header
        }

        if (nextAddress != null && nextLength != null) {
            val value = memory.readString(nextAddress, nextLength)
            headersDeserialized[nextIndex] = value

            nextAddress = null
            nextLength = null
            nextIndex += 1
        }
    }

    return headersDeserialized
}

fun byteArrayToInt(byteArray: ByteArray): Int {
    val result = ByteBuffer.wrap(byteArray)
    result.order(ByteOrder.LITTLE_ENDIAN)

    return result.getInt()
}
