package dev.theblckbird.rustedcomputer.registrate

import com.tterrag.registrate.AbstractRegistrate

class RustedRegistrate(modid: String) : AbstractRegistrate<RustedRegistrate>(modid) {
    companion object {
        fun create(modid: String): RustedRegistrate {
            val registrate = RustedRegistrate(modid)

            RustedRegistrateRegistrationCallback.provideRegistrate(registrate)

            return registrate
        }
    }
}