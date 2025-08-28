package dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class OpenScreenRequest(val computerPosition: BlockPos, val lines: Int) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<OpenScreenRequest>(
            ResourceLocation.fromNamespaceAndPath(
                RustedComputer.MODID,
                "get_stdout_request"
            )
        )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            OpenScreenRequest::computerPosition,
            ByteBufCodecs.VAR_INT,
            OpenScreenRequest::lines,
            ::OpenScreenRequest,
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}