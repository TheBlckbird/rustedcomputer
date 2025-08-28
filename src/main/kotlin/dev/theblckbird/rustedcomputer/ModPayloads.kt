package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen.CloseScreenHandler
import dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen.CloseScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen.OpenScreenHandler
import dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen.OpenScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.startprogram.StartProgramData
import dev.theblckbird.rustedcomputer.computer.networking.toserver.startprogram.StartProgramHandler
import dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin.StdinData
import dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin.StdinHandler
import dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout.StdoutData
import dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout.StdoutHandler
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(modid = RustedComputer.MODID)
object ModPayloads {
    @SubscribeEvent
    private fun registerPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")

        registrar.playToClient(
            StdoutData.TYPE,
            StdoutData.STREAM_CODEC,
            StdoutHandler::handleData,
        )

        registrar.playToServer(
            StdinData.TYPE,
            StdinData.STREAM_CODEC,
            StdinHandler::handleStdin,
        )

        registrar.playToServer(
            OpenScreenRequest.TYPE,
            OpenScreenRequest.STREAM_CODEC,
            OpenScreenHandler::handleRequest,
        )

        registrar.playToServer(
            StartProgramData.TYPE,
            StartProgramData.STREAM_CODEC,
            StartProgramHandler::handleStart,
        )

        registrar.playToServer(
            CloseScreenRequest.TYPE,
            CloseScreenRequest.STREAM_CODEC,
            CloseScreenHandler::handleRequest,
        )
    }
}