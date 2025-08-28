package dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class StdinData(val computerPosition: BlockPos, val content: String) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<StdinData>(
            ResourceLocation.fromNamespaceAndPath(
                RustedComputer.MODID,
                "stdin_data",
            )
        )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            StdinData::computerPosition,
            ByteBufCodecs.STRING_UTF8,
            StdinData::content,
            ::StdinData,
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}