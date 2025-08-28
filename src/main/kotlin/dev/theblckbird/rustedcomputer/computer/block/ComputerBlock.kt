package dev.theblckbird.rustedcomputer.computer.block

import com.mojang.serialization.MapCodec
import dev.theblckbird.rustedcomputer.ModBlockEntities
import dev.theblckbird.rustedcomputer.RelativeDirection
import dev.theblckbird.rustedcomputer.computer.ComputerScreen
import dev.theblckbird.rustedcomputer.computer.ComputerScreenHolder
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult

class ComputerBlock(properties: Properties) : HorizontalDirectionalBlock(properties), EntityBlock {
    companion object {
        val CODEC = simpleCodec(::ComputerBlock)

        fun getBlockEntity(level: ServerLevel, blockPos: BlockPos): ComputerBlockEntity? {
            val computer = level.getBlockEntity(blockPos)

            return if (computer == null || computer !is ComputerBlockEntity) {
                null
            } else {
                computer
            }
        }

        /**
         * Gets the relative direction of the block used for setting the redstone output for example.
         *
         * This is front/back/top/bottom/left/right.
         */
        fun getRelativeDirection(blockFacing: Direction, side: Direction): RelativeDirection {
            if (side == blockFacing) {
                return RelativeDirection.FRONT
            }

            if (side == blockFacing.opposite) {
                return RelativeDirection.BACK
            }

            if (side == Direction.UP) {
                return RelativeDirection.TOP
            }

            if (side == Direction.DOWN) {
                return RelativeDirection.BOTTOM
            }

            if (side == blockFacing.clockWise) {
                return RelativeDirection.RIGHT
            }

            if (side == blockFacing.counterClockWise) {
                return RelativeDirection.LEFT
            }

            throw UnsupportedOperationException()
        }
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)

        )
    }

    override fun codec(): MapCodec<out HorizontalDirectionalBlock> {
        return CODEC
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun newBlockEntity(
        blockPosition: BlockPos, state: BlockState
    ): BlockEntity {
        return ComputerBlockEntity(blockPosition, state)
    }

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            Minecraft.getInstance().execute {
                ComputerScreenHolder.screen = ComputerScreen(pos)
                Minecraft.getInstance().setScreen(ComputerScreenHolder.screen)
            }
        }

        return InteractionResult.SUCCESS
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        level: Level, state: BlockState, type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (type == ModBlockEntities.COMPUTER.get()) {
            BlockEntityTicker<ComputerBlockEntity> { level, blockPos, blockState, blockEntity ->
                if (blockEntity is ComputerBlockEntity) {
                    blockEntity.tick(level, blockPos, blockState, blockEntity)
                }
            } as BlockEntityTicker<T>
        } else {
            null
        }
    }

    override fun canConnectRedstone(
        state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?
    ): Boolean {
        return true
    }

    override fun isSignalSource(state: BlockState): Boolean {
        return true
    }

    override fun getSignal(blockState: BlockState, blockAccess: BlockGetter, pos: BlockPos, side: Direction): Int {
        val computer = blockAccess.getBlockEntity(pos)!! as ComputerBlockEntity

        val power = when(getRelativeDirection(blockState.getValue(FACING), side)) {
            RelativeDirection.TOP -> computer.powerLevels[RelativeDirection.TOP]
            RelativeDirection.BOTTOM -> computer.powerLevels[RelativeDirection.BOTTOM]
            RelativeDirection.LEFT -> computer.powerLevels[RelativeDirection.LEFT]
            RelativeDirection.RIGHT -> computer.powerLevels[RelativeDirection.RIGHT]
            RelativeDirection.FRONT -> computer.powerLevels[RelativeDirection.FRONT]
            RelativeDirection.BACK -> computer.powerLevels[RelativeDirection.BACK]
        }

        return power!!
    }
}