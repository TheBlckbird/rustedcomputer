package dev.theblckbird.rustedcomputer.computer.hostfunctions.redstone

import com.dylibso.chicory.annotations.HostModule
import com.dylibso.chicory.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure.Commands
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

@HostModule("redstone")
class RedstoneFunctions(val level: ServerLevel, val computerPosition: BlockPos) {
    @WasmExport
    fun setOutput(memory: Memory, sideLength: Int, sideOffset: Int, power: Int): Int {
        val side = memory.readString(sideOffset, sideLength)
        return Commands.pushCommand(SetRedstoneOutput(side, power, computerPosition))
    }

    @WasmExport
    fun getInput(memory: Memory, sideLength: Int, sideOffset: Int): Int {
        val side = memory.readString(sideOffset, sideLength)
        return Commands.pushCommand(GetRedstoneInput(side, computerPosition))
    }

    fun toHostFunctions(): Array<HostFunction> {
        return RedstoneFunctions_ModuleFactory.toHostFunctions(this)
    }
}