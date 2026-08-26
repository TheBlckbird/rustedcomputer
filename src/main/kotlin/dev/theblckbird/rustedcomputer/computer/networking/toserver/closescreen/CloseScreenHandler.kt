package dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen

import dev.theblckbird.rustedcomputer.computer.ComputerObservations
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext

object CloseScreenHandler {
    fun handleRequest(data: CloseScreenRequest, context: IPayloadContext) {
        assert(!context.player().level().isClientSide)

        ComputerBlock.getBlockEntity(context.player().level() as ServerLevel, data.computerPosition) ?: return

        ComputerObservations.removeObservingPlayer(context.player().uuid)
    }
}