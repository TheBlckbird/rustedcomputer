package dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen

import dev.theblckbird.rustedcomputer.computer.ComputerObservations
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext

object CloseScreenHandler {
    fun handleRequest(data: CloseScreenRequest, context: IPayloadContext) {
        assert(!context.player().level().isClientSide)

        val computer = ComputerBlock.getBlockEntity(context.player().level() as ServerLevel, data.computerPosition)

        if (computer == null) {
            return
        }

        ComputerObservations.removeObservingPlayer(context.player().uuid)
    }
}