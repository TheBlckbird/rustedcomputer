package dev.theblckbird.rustedcomputer.computer.hostfunctions.http

import com.dylibso.chicory.annotations.HostModule
import com.dylibso.chicory.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.computer.hostfunctions.HostFunctionsHelpers
import java.io.IOException
import java.net.ConnectException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@HostModule("http")
class HttpFunctions {
    private val httpClient by lazy {
        HttpClient.newBuilder().build()
    }

    @WasmExport
    fun fetch(
        instance: Instance,
        memory: Memory,
        method: Int,
        uriAddress: Int,
        uriLength: Int,
        bodyAddress: Int,
        bodyLength: Int,
        headersAddress: Int,
        headersLength: Int,
    ): Long {
        val rawUri = memory.readString(uriAddress, uriLength)
        val uri = URI(rawUri)

        val body = memory.readString(bodyAddress, bodyLength)
        val method = Method.fromInt(method)

        val headersAddresses = memory.readBytes(headersAddress, headersLength)
        val headers = deserializeHeaders(headersAddresses, memory)

        var requestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .method(method.toString(), HttpRequest.BodyPublishers.ofString(body))

        if (headers.isNotEmpty()) {
            requestBuilder = requestBuilder.headers(*headers)
        }

        val request = requestBuilder.build()

        try {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val responseSerialized = serializeResponse(response, memory, instance)

            return responseSerialized
        } catch (exception: IOException) {
            val returnAddress = HostFunctionsHelpers.allocateString("FIO", instance, memory)
            return returnAddress
        } catch (exception: ConnectException) {
            val returnAddress = HostFunctionsHelpers.allocateString("FC", instance, memory)
            return returnAddress
        } catch (exception: InterruptedException) {
            val returnAddress = HostFunctionsHelpers.allocateString("FI", instance, memory)
            return returnAddress
        } catch (exception: SecurityException) {
            val returnAddress = HostFunctionsHelpers.allocateString("FS", instance, memory)
            return returnAddress
        }
    }

    fun toHostFunctions(): Array<HostFunction> {
        return HttpFunctions_ModuleFactory.toHostFunctions(this)
    }
}
