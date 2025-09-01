package dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.world.level.Level
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import java.util.LinkedList
import java.util.Queue

typealias FutureId = Int

@EventBusSubscriber(modid = RustedComputer.MODID)
object Commands {
    private var commands: Queue<Pair<FutureId, Command>> = LinkedList()
    private var latestFutureId = 0

    /**
     * A list of all the futures that completed in the last tick.
     *
     * This will be cleaned up in the amount of ticks the integer specifies.
     */
    private var completedFutures = hashMapOf<FutureId, Pair<String, Int>>()

    @SubscribeEvent
    fun runAllCommands(event: ServerTickEvent.Pre) {
        for (completedFuture in completedFutures) {
            if (completedFuture.value.second == 0) {
                completedFutures.remove(completedFuture.key)
            } else {
                val (result, ticksRemaining) = completedFuture.value
                completedFuture.setValue(result to ticksRemaining - 1)
            }
        }

        var command: Pair<FutureId, Command>?

        while (commands.isNotEmpty()) {
            command = commands.peek()
            val result = command.second.run(event.server.getLevel(Level.OVERWORLD)!!)
            completedFutures[command.first] = result to 5
            commands.remove()
        }
    }

    /**
     * Pushes a command to the queue
     *
     * @return The future id needed to poll the result
     */
    fun pushCommand(command: Command): Int {
        latestFutureId += 1
        commands.add(latestFutureId to command)

        return latestFutureId
    }

    /**
     * Gets the future for a specific id.
     *
     * @return The future or `null` if it doesn't exist.
     */
    fun getFuture(futureId: FutureId): Future? {
        val completedFuture = completedFutures.remove(futureId)

        return if (completedFuture != null) {
            Future.Success(completedFuture.first)
        } else if (commands.any { it.first == futureId }) {
            Future.Pending
        } else {
            null
        }
    }
}