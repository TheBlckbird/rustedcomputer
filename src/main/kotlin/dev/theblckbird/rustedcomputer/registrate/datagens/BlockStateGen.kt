package dev.theblckbird.rustedcomputer.registrate.datagens

// Credit to https://github.com/Creators-of-Create/create/

import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import com.tterrag.registrate.util.nullness.NonNullBiConsumer
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Half
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile

object BlockStateGen {
    fun <T : Block> axisBlockProvider(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p -> axisBlock(c, p, getBlockModel(customItem, c, p)) }
    }


    fun <T : Block> directionalBlockProvider(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p ->
            p.directionalBlock(
                c.get(), getBlockModel(customItem, c, p).invoke(c.get().defaultBlockState())
            )
        }
    }


    fun <T : Block> directionalBlockProviderIgnoresWaterlogged(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p -> directionalBlockIgnoresWaterlogged(c, p, getBlockModel(customItem, c, p)) }
    }


    fun <T : Block> horizontalBlockProvider(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p ->
            p.horizontalBlock(
                c.get(), getBlockModel(customItem, c, p).invoke(c.get().defaultBlockState())
            )
        }
    }


    fun <T : Block> horizontalAxisBlockProvider(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p -> horizontalAxisBlock(c, p, getBlockModel(customItem, c, p)) }
    }


    fun <T : Block> simpleCubeAll(path: String): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p -> p.simpleBlock(c.get(), p.models().cubeAll(c.name, p.modLoc("block/$path"))) }
    }


    fun <T : Block> horizontalWheelProvider(customItem: Boolean): NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p -> horizontalWheel(c, p, getBlockModel(customItem, c, p)) }
    }

    // Utility

    private fun <T : Block> getBlockModel(
        customItem: Boolean, c: DataGenContext<Block, T>, p: RegistrateBlockstateProvider
    ): (BlockState) -> ModelFile {
        return { if (customItem) AssetLookup.partialBaseModel(c, p) else AssetLookup.standardModel(c, p) }
    }

    // Generators


    fun <T : Block> directionalBlockIgnoresWaterlogged(
        ctx: DataGenContext<Block, T>, prov: RegistrateBlockstateProvider, modelFunc: (BlockState) -> ModelFile
    ) {
        prov.getVariantBuilder(ctx.entry).forAllStatesExcept({ state ->
            val dir = state.getValue(BlockStateProperties.FACING)
            ConfiguredModel.builder().modelFile(modelFunc(state))
                .rotationX(if (dir == Direction.DOWN) 180 else if (dir.axis.isHorizontal) 90 else 0)
                .rotationY(if (dir.axis.isVertical) 0 else ((dir.toYRot().toInt()) + 180) % 360).build()
        }, BlockStateProperties.WATERLOGGED)
    }


    @JvmOverloads
    fun <T : Block> axisBlock(
        ctx: DataGenContext<Block, T>,
        prov: RegistrateBlockstateProvider,
        modelFunc: (BlockState) -> ModelFile,
        uvLock: Boolean = false
    ) {
        prov.getVariantBuilder(ctx.entry).forAllStatesExcept({ state ->
            val axis = state.getValue(BlockStateProperties.AXIS)
            ConfiguredModel.builder().modelFile(modelFunc(state)).uvLock(uvLock)
                .rotationX(if (axis == Axis.Y) 0 else 90)
                .rotationY(if (axis == Axis.X) 90 else if (axis == Axis.Z) 180 else 0).build()
        }, BlockStateProperties.WATERLOGGED)
    }


    fun <T : Block> simpleBlock(
        ctx: DataGenContext<Block, T>, prov: RegistrateBlockstateProvider, modelFunc: (BlockState) -> ModelFile
    ) {
        prov.getVariantBuilder(ctx.entry).forAllStatesExcept({ state ->
            ConfiguredModel.builder().modelFile(modelFunc(state)).build()
        }, BlockStateProperties.WATERLOGGED)
    }


    fun <T : Block> horizontalAxisBlock(
        ctx: DataGenContext<Block, T>, prov: RegistrateBlockstateProvider, modelFunc: (BlockState) -> ModelFile
    ) {
        prov.getVariantBuilder(ctx.entry).forAllStates { state ->
            val axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS)
            ConfiguredModel.builder().modelFile(modelFunc(state)).rotationY(if (axis == Axis.X) 90 else 0).build()
        }
    }


    fun <T : Block> horizontalWheel(
        ctx: DataGenContext<Block, T>, prov: RegistrateBlockstateProvider, modelFunc: (BlockState) -> ModelFile
    ) {
        prov.getVariantBuilder(ctx.get()).forAllStates { state ->
            ConfiguredModel.builder().modelFile(modelFunc(state)).rotationX(90)
                .rotationY(((state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot().toInt()) + 180) % 360)
                .build()
        }
    }


    fun <T : Block> cubeAll(
        ctx: DataGenContext<Block, T>,
        prov: RegistrateBlockstateProvider,
        textureSubDir: String,
        name: String = ctx.name
    ) {
        val texturePath = "block/$textureSubDir$name"
        prov.simpleBlock(ctx.get(), prov.models().cubeAll(ctx.name, prov.modLoc(texturePath)))
    }


    fun <P : Block> naturalStoneTypeBlock(type: String): NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p ->
            val variants = Array(4) { i ->
                ConfiguredModel.builder().modelFile(
                    p.models()
                        .cubeAll("${type}_natural_$i", p.modLoc("block/palettes/stone_types/natural/${type}_$i"))
                ).buildLast()
            }
            p.getVariantBuilder(c.get()).partialState().setModels(*variants)
        }
    }


    fun <P : TrapDoorBlock> uvLockedTrapdoorBlock(
        block: P, bottom: ModelFile, top: ModelFile, open: ModelFile
    ): NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> {
        return NonNullBiConsumer { c, p ->
            p.getVariantBuilder(block).forAllStatesExcept({ state ->
                val xRot = 0
                var yRot = (state.getValue(TrapDoorBlock.FACING).toYRot().toInt()) + 180
                val isOpen = state.getValue(TrapDoorBlock.OPEN)
                if (!isOpen) yRot = 0
                yRot %= 360
                ConfiguredModel.builder()
                    .modelFile(if (isOpen) open else if (state.getValue(TrapDoorBlock.HALF) == Half.TOP) top else bottom)
                    .rotationX(xRot).rotationY(yRot).uvLock(!isOpen).build()
            }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED)
        }
    }


    fun mapToAir(p: RegistrateBlockstateProvider): (BlockState) -> Array<ConfiguredModel> {
        return { _ ->
            ConfiguredModel.builder().modelFile(p.models().getExistingFile(p.mcLoc("block/air"))).build()
        }
    }
}