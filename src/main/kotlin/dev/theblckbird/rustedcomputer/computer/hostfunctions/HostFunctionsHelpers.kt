package dev.theblckbird.rustedcomputer.computer.hostfunctions

import com.dylibso.chicory.runtime.ExportFunction
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.RustedComputer

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
     * Allocates a string in WASM memory and returns the combined pointer and length.
     *
     * @return The pointer and length combined to a singular `Long`
     */
    fun allocateString(content: String, alloc: ExportFunction, memory: Memory): Long {
        val length = content.toByteArray().count()
        val pointer = alloc.apply(length.toLong())[0].toInt()
        memory.writeString(pointer, content)

        return combinePointerAndLength(pointer, length)
    }
}