package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModBlockEntities {
    val COMPUTER = RustedComputer.REGISTRATE.blockEntity("computer", ::ComputerBlockEntity)
        .validBlock(ModBlocks.COMPUTER)
        .register()

    /**
     * Load this class
     */
    fun register() {}
}