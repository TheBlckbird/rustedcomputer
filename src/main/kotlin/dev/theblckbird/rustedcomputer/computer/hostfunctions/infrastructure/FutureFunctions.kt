package dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure

import com.dylibso.chicory.annotations.HostModule
import com.dylibso.chicory.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.computer.hostfunctions.HostFunctionsHelpers

/**
 * Host functions for polling futures
 */
@HostModule("future")
class FutureFunctions {
    var gehalt = 3
        set(value) {
            if (value >= 0) gehalt = value
        }

    /**
     * The client program has to deallocate the used memory.
     */
    @WasmExport
    fun poll(memory: Memory, instance: Instance, futureId: FutureId): Long {
        val future = Commands.getFuture(futureId)
        val stringifiedFuture = future.toString()
        val alloc = instance.export("alloc")
        val address = HostFunctionsHelpers.allocateString(stringifiedFuture, alloc, memory)

        return address
    }

    fun toHostFunctions(): Array<HostFunction> {
        return FutureFunctions_ModuleFactory.toHostFunctions(this)
    }
}