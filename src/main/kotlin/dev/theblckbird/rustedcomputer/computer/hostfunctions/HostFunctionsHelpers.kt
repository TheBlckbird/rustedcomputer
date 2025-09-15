package dev.theblckbird.rustedcomputer.computer.hostfunctions

import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Memory

object HostFunctionsHelpers {
    /**
     * Combines a pointer (u32, represented as an `Int` so it can be passed to WASM) and a length to a `Long`.
     *
     * @return The pointer and length combined
     */
    fun combinePointerAndLength(pointer: Int, length: Int): Long {
        return (pointer.toLong() shl 32) or (length.toLong() and 0xFFFFFFFFL)
    }

    /**
     * Writes a string to WASM memory and returns the combined pointer and length.
     *
     * @return The pointer and length combined to a singular `Long`
     */
    fun allocateString(content: String, instance: Instance, memory: Memory): Long {
        return allocateByteArray(content.toByteArray(), instance, memory)
    }

    /**
     * Writes a `ByteArray` to WASM memory and returns the combined pointer and length.
     *
     * @return The pointer and length combined to a singular `Long`
     */
    fun allocateByteArray(content: ByteArray, instance: Instance, memory: Memory): Long {
        val alloc = instance.export("alloc")
        val length = content.count()
        val pointer = alloc.apply(length.toLong())[0].toInt()
        memory.write(pointer, content)

        return combinePointerAndLength(pointer, length)
    }
}