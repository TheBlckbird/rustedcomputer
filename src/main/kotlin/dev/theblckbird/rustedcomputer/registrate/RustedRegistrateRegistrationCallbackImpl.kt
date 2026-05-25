package dev.theblckbird.rustedcomputer.registrate

import com.mojang.datafixers.util.Either
import com.tterrag.registrate.util.nullness.NonNullConsumer
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.ArrayList
import java.util.HashMap


/**
 * Credit to https://github.com/Creators-of-Create/create MIT licensed
 */
object RustedRegistrateRegistrationCallbackImpl {

    // Intentionally not a synchronized map, since all safe accesses have to be synchronized anyway.
    private val CALLBACKS = HashMap<String, Either<MutableList<CallbackImpl<*, *>>, RustedRegistrate>>()

    fun provideRegistrate(registrate: RustedRegistrate) {
        synchronized(CALLBACKS) {
            val modid = registrate.modid

            val either = CALLBACKS.remove(modid)
            if (either != null) {
                val optionalCallbacks = either.left()
                if (optionalCallbacks.isEmpty) { // in other words, either.right().isPresent()
                    throw IllegalArgumentException("Tried to register a duplicate RustedRegistrate instance for mod ID: $modid")
                }

                for (callback in optionalCallbacks.get()) {
                    callback.addToRegistrate(registrate)
                }
            }

            CALLBACKS[modid] = Either.right(registrate)
        }
    }

    fun <R, T : R> register(
        registry: ResourceKey<out Registry<R>>,
        id: ResourceLocation,
        callback: NonNullConsumer<in T>
    ) {
        val callbackImpl = CallbackImpl(registry, id, callback)

        val either: Either<MutableList<CallbackImpl<*, *>>, RustedRegistrate>
        synchronized(CALLBACKS) {
            either = CALLBACKS.computeIfAbsent(id.namespace) { _ ->
                Either.left(ArrayList())
            }
            // must be synchronized here, because if `registerRegistrate` were called between these two calls,
            // we would be adding to a list that would never be used
            either.ifLeft { callbacks -> callbacks.add(callbackImpl) }
        }

        // This is safe to call outside the synchronized block, because a registrate will only ever be added once.
        either.ifRight { registrate -> callbackImpl.addToRegistrate(registrate) }
    }

    private data class CallbackImpl<R, T : R>(
        val registry: ResourceKey<out Registry<R>>,
        val id: ResourceLocation,
        val callback: NonNullConsumer<in T>
    ) {
        // Helper method so javac doesn't explode on the generic types.
        // Otherwise, IntelliJ does type inference better than javac,
        // and everything becomes illegible with generic erasure casts.
        fun addToRegistrate(registrate: RustedRegistrate) {
            registrate.addRegisterCallback<R, T>(id.path, registry, callback)
        }
    }
}