package dev.theblckbird.rustedcomputer.registrate.datagens

// Credit to https://github.com/Creators-of-Create/create/

import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import com.tterrag.registrate.providers.RegistrateItemModelProvider
import com.tterrag.registrate.util.nullness.NonNullBiConsumer
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ModelFile

object AssetLookup {

    /**
     * Custom block models packaged with other partials. Example:
     * models/block/schematicannon/block.json <br></br>
     * <br></br>
     * Adding "powered", "vertical" will look for /block_powered_vertical.json
     */
    @JvmStatic
    fun partialBaseModel(
        ctx: DataGenContext<*, *>,
        prov: RegistrateBlockstateProvider,
        vararg suffix: String
    ): ModelFile {
        var string = "/block"
        for (suf in suffix) {
            if (suf.isNotEmpty()) {
                string += "_$suf"
            }
        }
        val location = "block/${ctx.name}$string"
        return prov.models().getExistingFile(prov.modLoc(location))
    }

    /**
     * Custom block model from models/block/x.json
     */
    @JvmStatic
    fun standardModel(ctx: DataGenContext<*, *>, prov: RegistrateBlockstateProvider): ModelFile {
        return prov.models().getExistingFile(prov.modLoc("block/${ctx.name}"))
    }

    /**
     * Generate item model inheriting from a separate model in
     * models/block/x/item.json
     */
    @JvmStatic
    fun <I : BlockItem> customItemModel(
        ctx: DataGenContext<Item, I>,
        prov: RegistrateItemModelProvider
    ): ItemModelBuilder {
        return prov.blockItem({ ctx.entry.getBlock() }, "/item")
    }

    /**
     * Generate item model inheriting from a separate model in
     * models/block/folders[0]/folders[1]/.../item.json "_" will be replaced by the
     * item name
     */
    @JvmStatic
    fun <I : BlockItem> customBlockItemModel(
        vararg folders: String
    ): NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> {
        return NonNullBiConsumer { c, p ->
            var path = "block"
            for (string in folders) {
                path += "/" + if ("_" == string) c.name else string
            }
            p.withExistingParent(c.name, p.modLoc(path))
        }
    }

    @JvmStatic
    fun <I : Item> customGenericItemModel(
        vararg folders: String
    ): NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> {
        return NonNullBiConsumer { c, p ->
            var path = "block"
            for (string in folders) {
                path += "/" + if ("_" == string) c.name else string
            }
            p.withExistingParent(c.name, p.modLoc(path))
        }
    }

    @JvmStatic
    fun forPowered(
        ctx: DataGenContext<*, *>,
        prov: RegistrateBlockstateProvider
    ): (BlockState) -> ModelFile {
        return { state ->
            if (state.getValue(BlockStateProperties.POWERED))
                partialBaseModel(ctx, prov, "powered")
            else
                partialBaseModel(ctx, prov)
        }
    }

    @JvmStatic
    fun forPowered(
        ctx: DataGenContext<*, *>,
        prov: RegistrateBlockstateProvider,
        path: String
    ): (BlockState) -> ModelFile {
        return { state ->
            prov.models().getExistingFile(
                prov.modLoc("block/$path${if (state.getValue(BlockStateProperties.POWERED)) "_powered" else ""}")
            )
        }
    }

    @JvmStatic
    fun withIndicator(
        ctx: DataGenContext<*, *>,
        prov: RegistrateBlockstateProvider,
        baseModelFunc: (BlockState) -> ModelFile,
        property: IntegerProperty
    ): (BlockState) -> ModelFile {
        return { state ->
            val baseModel = baseModelFunc(state).location
            val integer = state.getValue(property)
            prov.models()
                .withExistingParent("${ctx.name}_$integer", baseModel)
                .texture("indicator", "block/indicator/$integer")
        }
    }

    @JvmStatic
    fun <T : Item> existingItemModel(): NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> {
        return NonNullBiConsumer { c, p -> p.getExistingFile(p.modLoc("item/${c.name}")) }
    }

    @JvmStatic
    fun <T : Item> itemModel(name: String): NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> {
        return NonNullBiConsumer { c, p -> p.getExistingFile(p.modLoc("item/$name")) }
    }

    @JvmStatic
    fun <T : Item> itemModelWithPartials(): NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> {
        return NonNullBiConsumer { c, p -> p.withExistingParent("item/${c.name}", p.modLoc("item/${c.name}/item")) }
    }
}