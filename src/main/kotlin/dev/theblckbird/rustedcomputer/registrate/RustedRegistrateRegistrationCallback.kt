package dev.theblckbird.rustedcomputer.registrate

import com.tterrag.registrate.util.nullness.NonNullConsumer
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

/**
 * Register a callback for when an entry is added to any [RustedRegistrate] instance
 *
 * Credit to https://github.com/Creators-of-Create/create MIT licensed
 */
object RustedRegistrateRegistrationCallback {
    fun <R, T : R> register(
        registry: ResourceKey<out Registry<R>>,
        id: ResourceLocation,
        callback: NonNullConsumer<in T>
    ) {
        RustedRegistrateRegistrationCallbackImpl.register<R, T>(registry, id, callback)
    }

    /**
     * Provide a [RustedRegistrate] instance to be used by the API.
     * Instances created by [RustedRegistrate.create] will automatically be registered.
     * It is illegal to call this method more than once for the same mod ID.
     */
    fun provideRegistrate(registrate: RustedRegistrate) {
        RustedRegistrateRegistrationCallbackImpl.provideRegistrate(registrate)
    }
}