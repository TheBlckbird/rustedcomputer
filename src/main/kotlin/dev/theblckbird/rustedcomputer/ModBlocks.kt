package dev.theblckbird.rustedcomputer

import com.tterrag.registrate.providers.RegistrateRecipeProvider.has
import dev.theblckbird.rustedcomputer.computer.block.ComputerBlock
import dev.theblckbird.rustedcomputer.registrate.datagens.BlockStateGen
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

object ModBlocks {
    val COMPUTER = RustedComputer.REGISTRATE.block("computer", ::ComputerBlock)
        .initialProperties { Blocks.STONE }
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .recipe { context, provider ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, context.get(), 1)
                .pattern("ORB")
                .pattern("IGI")
                .pattern("IDI")
                .define('B', Items.STONE_BUTTON)
                .define('G', Items.GLASS_PANE)
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('O', Items.GOLD_INGOT)
                .define('D', Items.DIAMOND)
                .unlockedBy("has_" + context.name, has(context.get()))
                .save(provider)
        }
        .simpleItem()
        .lang("Rusty Computer")
        .register()

    /**
     * Load this class
     */
    fun register() {}
}