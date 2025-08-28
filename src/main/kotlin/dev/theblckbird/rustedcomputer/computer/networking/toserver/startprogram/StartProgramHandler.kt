package dev.theblckbird.rustedcomputer.computer.networking.toserver.startprogram

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext

object StartProgramHandler {
    fun handleStart(data: StartProgramData, context: IPayloadContext) {
        assert(!context.player().level().isClientSide)

        val computer = ComputerBlock.getBlockEntity(context.player().level() as ServerLevel, data.computerPosition)

        if (computer == null) {
            return
        }

        computer.startProgram(context.player().level() as ServerLevel, data.fileName)
    }
}