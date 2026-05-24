package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.ComputerIdComponent
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object DataComponents {
    val REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RustedComputer.MODID)

    val COMPUTER_COMPONENT = REGISTRAR.registerComponentType(
        "computer"
    ) { builder ->
        builder
            .persistent(ComputerIdComponent.CODEC)
            .networkSynchronized(ComputerIdComponent.STREAM_CODEC)
    }

    fun register(modEventBus: IEventBus) {
        REGISTRAR.register(modEventBus)
    }
}