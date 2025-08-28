package dev.theblckbird.rustedcomputer.computer.hostfunctions

import com.dylibso.chicory.annotations.HostModule
import com.dylibso.chicory.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import dev.theblckbird.rustedcomputer.RustedComputer

@HostModule("rustedcomputer")
final class HostFunctions {
    /**
     * This is kept in as an easter egg
     */
    @WasmExport
    fun tom() {
        RustedComputer.Companion.LOGGER.info("Tooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooomm")
    }

    fun toHostFunctions(): Array<HostFunction> {
        return HostFunctions_ModuleFactory.toHostFunctions(this)
    }
}