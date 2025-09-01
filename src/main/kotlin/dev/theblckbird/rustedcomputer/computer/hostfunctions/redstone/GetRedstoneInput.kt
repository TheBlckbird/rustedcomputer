package dev.theblckbird.rustedcomputer.computer.hostfunctions.redstone

import dev.theblckbird.rustedcomputer.RelativeDirection
import dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure.Command
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.HorizontalDirectionalBlock

class GetRedstoneInput(
    private val side: String,
    private val computerPosition: BlockPos
) : Command {
    override fun run(level: ServerLevel): String {
        val relativeSide = RelativeDirection.fromString(side)

        if (relativeSide == null) {
            return null.toString()
        }

        val absoluteDirection = getAbsoluteDirection(
            level.getBlockState(computerPosition).getValue(
                HorizontalDirectionalBlock.FACING
            ), relativeSide
        )

        return level.getSignal(computerPosition, absoluteDirection).toString()
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
}