package dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext

object StdinHandler {
    fun handleStdin(data: StdinData, context: IPayloadContext) {
        assert(!context.player().level().isClientSide)

        val computer =
            ComputerBlock.getBlockEntity(context.player().level() as ServerLevel, data.computerPosition) ?: return

        computer.writeStdin(data.content + '\n')
    }
}