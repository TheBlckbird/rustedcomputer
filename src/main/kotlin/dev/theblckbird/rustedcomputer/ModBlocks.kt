package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModBlocks {
    private val BLOCKS = DeferredRegister.createBlocks(RustedComputer.MODID)

    val COMPUTER = BLOCKS.register(
        "computer",
        Supplier { ComputerBlock(BlockBehaviour.Properties.of()) }
    )

    // Load this class
    fun register(modEventBus: IEventBus) {
        BLOCKS.register(modEventBus)
    }
}