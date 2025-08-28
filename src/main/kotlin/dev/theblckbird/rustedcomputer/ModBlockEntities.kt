package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModBlockEntities {
    private val BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RustedComputer.MODID)

    val COMPUTER = BLOCK_ENTITIES.register(
        "computer",
        Supplier {
            BlockEntityType.Builder.of(
                ::ComputerBlockEntity,
                ModBlocks.COMPUTER.get(),
            ).build(null)
        }
    )
//    val COMPUTER = RustedComputer.REGISTRATE.blockEntity("computer", ::ComputerBlockEntity)
//        .validBlock(ModBlocks.COMPUTER)
//        .register()

    fun register(modEventBus: IEventBus) {
        BLOCK_ENTITIES.register(modEventBus)
    }
}