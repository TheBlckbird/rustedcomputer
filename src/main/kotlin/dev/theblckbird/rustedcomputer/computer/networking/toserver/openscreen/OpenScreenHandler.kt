package dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen

import dev.theblckbird.rustedcomputer.computer.ComputerObservations
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout.StdoutData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext

object OpenScreenHandler {
    fun handleRequest(data: OpenScreenRequest, context: IPayloadContext) {
        assert(!context.player().level().isClientSide)

        val computer = ComputerBlock.getBlockEntity(context.player().level() as ServerLevel, data.computerPosition)

        if (computer == null) {
            return
        }

        ComputerObservations.addObservingPlayer(context.player().uuid, computer.blockPos)

        PacketDistributor.sendToPlayer(
            context.player() as ServerPlayer,
            StdoutData(data.computerPosition, computer.getStdout())
        )
    }
}