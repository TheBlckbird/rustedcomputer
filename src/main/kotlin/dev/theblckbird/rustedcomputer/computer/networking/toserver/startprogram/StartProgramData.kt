package dev.theblckbird.rustedcomputer.computer.networking.toserver.startprogram

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class StartProgramData(val computerPosition: BlockPos, val fileName: String) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<StartProgramData>(
            ResourceLocation.fromNamespaceAndPath(
                RustedComputer.MODID,
                "start_program_data",
            )
        )

        val STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            StartProgramData::computerPosition,
            ByteBufCodecs.STRING_UTF8,
            StartProgramData::fileName,
            ::StartProgramData,
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}