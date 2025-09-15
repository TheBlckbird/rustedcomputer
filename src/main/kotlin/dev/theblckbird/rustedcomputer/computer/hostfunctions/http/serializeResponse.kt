package dev.theblckbird.rustedcomputer.computer.hostfunctions.http

import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.hostfunctions.HostFunctionsHelpers
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpResponse
import java.nio.ByteBuffer

/**
 * Serializes the response per the documentation.
 *
 * Returns the combined pointer and length of a byte array containing all the response data.
 * The caller has to manage deallocation of the memory.
 */
fun serializeResponse(response: HttpResponse<String>, memory: Memory, instance: Instance): Long {
    val statusCode = response.statusCode().toUShort()
    val headersSerialized = serializeHeaders(response.headers(), instance, memory)

    val version: Byte = when (response.version()) {
        HttpClient.Version.HTTP_1_1 -> 11
        HttpClient.Version.HTTP_2 -> 2
    }

    val headersPointerLength =
        HostFunctionsHelpers.allocateByteArray(longsToByteArray(headersSerialized), instance, memory)
    val headersPointerLengthAsBytes = ByteBuffer.allocate(8)
    headersPointerLengthAsBytes.putLong(headersPointerLength)

    val responseSerialized = byteArrayOf(
        (statusCode.toInt() shr 8).toByte(),
        (statusCode.toInt() and 0xFF).toByte(),
        version,
    ) + headersPointerLengthAsBytes.array() + response.body().toByteArray()

    val responsePointer = HostFunctionsHelpers.allocateByteArray(responseSerialized, instance, memory)

    return responsePointer
}

private fun serializeHeaders(headers: HttpHeaders, instance: Instance, memory: Memory): List<Long> {
    val outputAddresses = mutableListOf<Long>()

    for ((headerName, headerValues) in headers.map()) {
        val headerNamePointer = HostFunctionsHelpers.allocateString(headerName, instance, memory)

        for (headerValue in headerValues) {
            val headerValuePointer = HostFunctionsHelpers.allocateString(headerValue, instance, memory)
            outputAddresses.add(headerNamePointer)
            outputAddresses.add(headerValuePointer)
        }
    }

    return outputAddresses
}

private fun longsToByteArray(longs: List<Long>): ByteArray {
    val buffer = ByteBuffer.allocate(longs.size * 8)
    longs.forEach { buffer.putLong(it) }
    return buffer.array()
}
