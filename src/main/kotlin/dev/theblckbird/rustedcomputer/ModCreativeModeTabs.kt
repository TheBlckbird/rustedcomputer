package dev.theblckbird.rustedcomputer

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModCreativeModeTabs {
    val TAB_REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RustedComputer.MODID)

    val BASE_CREATIVE_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = TAB_REGISTRY.register("base", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.${RustedComputer.MODID}.base"))
            .icon { ItemStack(ModItems.COMPUTER.get()) }
            .displayItems { _, output ->
                output.accept(ModItems.COMPUTER.get())
                output.accept(ModBlocks.COMPUTER.get())
            }
            .build()
    })

    fun register(modEventBus: IEventBus) {
        TAB_REGISTRY.register(modEventBus)
    }
}