package dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout

import dev.theblckbird.rustedcomputer.computer.ComputerScreenHolder
import net.neoforged.neoforge.network.handling.IPayloadContext

object StdoutHandler {
    fun handleData(data: StdoutData, context: IPayloadContext) {
        if (ComputerScreenHolder.screen?.terminal?.getStdout() == null) {
            return
        }

        ComputerScreenHolder.screen?.terminal?.appendStdout(data.content)
    }
}