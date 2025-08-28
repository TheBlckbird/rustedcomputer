package dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class StdoutData(val computerPosition: BlockPos, val content: String) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<StdoutData>(
            ResourceLocation.fromNamespaceAndPath(
                RustedComputer.Companion.MODID,
                "stdout_data"
            )
        )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            StdoutData::computerPosition,
            ByteBufCodecs.STRING_UTF8,
            StdoutData::content,
            ::StdoutData,
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}