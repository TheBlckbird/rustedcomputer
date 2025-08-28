package dev.theblckbird.rustedcomputer

import com.mojang.logging.LogUtils
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(RustedComputer.MODID)
class RustedComputer(modEventBus: IEventBus) {
    companion object {
        const val MODID = "rustedcomputer"
        val LOGGER: org.slf4j.Logger = LogUtils.getLogger()
    }

    init {
        LOGGER.info("Hello from Create: More Chocolate")

        ModItems.register(modEventBus)
        ModBlocks.register(modEventBus)
        ModBlockEntities.register(modEventBus)

        ModCreativeModeTabs.register(modEventBus)
    }
}