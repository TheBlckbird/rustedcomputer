package dev.theblckbird.rustedcomputer

import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import dev.theblckbird.rustedcomputer.registrate.datagens.BlockStateGen
import net.minecraft.world.level.block.Blocks

object ModBlocks {
    val COMPUTER = RustedComputer.REGISTRATE.block("computer", ::ComputerBlock)
        .initialProperties { Blocks.STONE }
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .simpleItem()
        .lang("Rusty Computer")
        .register()

    /**
     * Load this class
     */
    fun register() {}
}