package dev.theblckbird.rustedcomputer.computer.hostfunctions.redstone

import com.dylibso.chicory.annotations.HostModule
import com.dylibso.chicory.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Memory
import dev.theblckbird.rustedcomputer.RelativeDirection
import dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure.Commands
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.HorizontalDirectionalBlock

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

        val powerSide = when (side) {
            "top" -> RelativeDirection.TOP
            "bottom" -> RelativeDirection.BOTTOM
            "left" -> RelativeDirection.LEFT
            "right" -> RelativeDirection.RIGHT
            "front" -> RelativeDirection.FRONT
            "back" -> RelativeDirection.BACK
            else -> null
        }

        val absoluteDirection = getAbsoluteDirection(level.getBlockState(computerPosition).getValue(
            HorizontalDirectionalBlock.FACING), powerSide!!)

        return level.getSignal(computerPosition, absoluteDirection)
    }

    fun getAbsoluteDirection(blockFacing: Direction, relativeDirection: RelativeDirection): Direction {
        return when (relativeDirection) {
            RelativeDirection.TOP -> Direction.UP
            RelativeDirection.BOTTOM -> Direction.DOWN
            RelativeDirection.LEFT -> blockFacing.counterClockWise
            RelativeDirection.RIGHT -> blockFacing.clockWise
            RelativeDirection.FRONT -> blockFacing
            RelativeDirection.BACK -> blockFacing.opposite
        }
    }

    fun toHostFunctions(): Array<HostFunction> {
        return RedstoneFunctions_ModuleFactory.toHostFunctions(this)
    }
}