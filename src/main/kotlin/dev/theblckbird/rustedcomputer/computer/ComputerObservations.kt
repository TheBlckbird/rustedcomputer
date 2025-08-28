package dev.theblckbird.rustedcomputer.computer

import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import java.util.UUID

/**
 * Stores which players are currently looking at which computers.
 */
@EventBusSubscriber(modid = RustedComputer.MODID)
object ComputerObservations {
    private var playersObservingComputers: HashMap<UUID, BlockPos> = hashMapOf()

    /**
     * Gets the computer the player is currently observing or null.
     */
    fun getObservedComputer(playerUuid: UUID, level: ServerLevel): ComputerBlockEntity? {
        val computerPosition = playersObservingComputers.get(playerUuid)
        if (computerPosition == null) {
            return null
        }

        val computer = level.getBlockEntity(computerPosition)

        return if (computer == null || computer !is ComputerBlockEntity) {
            null
        } else {
            computer
        }
    }

    /**
     * Gets a list of the player's UUIDs currently observing a specific computer.
     */
    fun getListObserving(searchedComputerPosition: BlockPos): HashSet<UUID> {
        var players = hashSetOf<UUID>()

        for ((playerUuid, computerPosition) in playersObservingComputers) {
            if (computerPosition == searchedComputerPosition) {
                players.add(playerUuid)
            }
        }

        return players
    }

    /**
     * Adds an entry that this player is currently observing the computer at the specified position.
     */
    fun addObservingPlayer(playerUuid: UUID, computerPosition: BlockPos) {
        playersObservingComputers[playerUuid] = computerPosition
    }

    /**
     * Marks the player as "not observing anything".
     */
    fun removeObservingPlayer(playerUuid: UUID) {
        playersObservingComputers.remove(playerUuid)
    }

    /**
     * Removes every observation of the computer position.
     */
    fun removeComputerPosition(computerPositionToRemove: BlockPos) {
        for ((playerUuid, computerPosition) in playersObservingComputers) {
            if (computerPosition == computerPositionToRemove) {
                playersObservingComputers.remove(playerUuid)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerDisconnect(event: PlayerEvent.PlayerLoggedOutEvent) {
        removeObservingPlayer(event.entity.uuid)
    }

    @SubscribeEvent
    fun onPlayerDeath(event: LivingDeathEvent) {
        if (event.entity is Player) {
            removeObservingPlayer(event.entity.uuid)
        }
    }
}