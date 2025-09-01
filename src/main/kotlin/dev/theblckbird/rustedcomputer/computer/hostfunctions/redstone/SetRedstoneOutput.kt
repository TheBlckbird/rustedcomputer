package dev.theblckbird.rustedcomputer.computer.hostfunctions.redstone

import dev.theblckbird.rustedcomputer.ModBlocks
import dev.theblckbird.rustedcomputer.RelativeDirection
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure.Command
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block

class SetRedstoneOutput(
    private val side: String,
    private val power: Int,
    private val computerPosition: BlockPos
) : Command {
    override fun run(level: ServerLevel): String {
        val blockState = level.getBlockState(computerPosition)
        val computer = ComputerBlock.getBlockEntity(level, computerPosition)

        if (computer == null) {
            return null.toString()
        }

        when (side) {
            "top" -> computer.powerLevels[RelativeDirection.TOP] = power
            "bottom" -> computer.powerLevels[RelativeDirection.BOTTOM] = power
            "left" -> computer.powerLevels[RelativeDirection.LEFT] = power
            "right" -> computer.powerLevels[RelativeDirection.RIGHT] = power
            "front" -> computer.powerLevels[RelativeDirection.FRONT] = power
            "back" -> computer.powerLevels[RelativeDirection.BACK] = power
        }

        level.setBlock(computerPosition, blockState, Block.UPDATE_NEIGHBORS)
        level.updateNeighborsAt(computerPosition, ModBlocks.COMPUTER.get())

        return null.toString()
    }
}