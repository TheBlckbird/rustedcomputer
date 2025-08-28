package dev.theblckbird.rustedcomputer

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    private val ITEMS = DeferredRegister.createItems(RustedComputer.MODID)

    val COMPUTER = ITEMS.registerSimpleBlockItem(ModBlocks.COMPUTER)

    // Load this class
    fun register(modEventBus: IEventBus) {
        ITEMS.register(modEventBus)
    }
}