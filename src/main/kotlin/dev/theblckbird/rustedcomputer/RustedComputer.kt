package dev.theblckbird.rustedcomputer

import com.mojang.logging.LogUtils
import dev.theblckbird.rustedcomputer.registrate.RustedRegistrate
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(RustedComputer.MODID)
class RustedComputer(modEventBus: IEventBus) {
    companion object {
        const val MODID = "rustedcomputer"
        val LOGGER: org.slf4j.Logger = LogUtils.getLogger()

        val REGISTRATE by lazy { RustedRegistrate.create(MODID) }
    }

    init {
        LOGGER.info("Hello from Create: More Chocolate")
        REGISTRATE.registerEventListeners(modEventBus)

        ModBlocks.register()
        ModBlockEntities.register()

        DataComponents.register(modEventBus)
    }
}