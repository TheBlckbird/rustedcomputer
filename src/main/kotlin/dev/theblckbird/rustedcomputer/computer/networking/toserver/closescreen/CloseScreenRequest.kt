package dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class CloseScreenRequest(val computerPosition: BlockPos) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<CloseScreenRequest>(
            ResourceLocation.fromNamespaceAndPath(
                RustedComputer.MODID,
                "close_screen_request"
            )
        )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CloseScreenRequest::computerPosition,
            ::CloseScreenRequest,
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}