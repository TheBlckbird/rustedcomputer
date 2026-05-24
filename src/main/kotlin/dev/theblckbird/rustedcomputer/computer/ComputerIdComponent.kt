package dev.theblckbird.rustedcomputer.computer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.ByteBufCodecs

data class ComputerIdComponent(val uuid: String) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("uuid").forGetter(ComputerIdComponent::uuid)
            ).apply(instance, ::ComputerIdComponent)
        }

        val STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC)
    }
}