package dev.theblckbird.rustedcomputer.helpers

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource
import java.io.File

object SaveFileHelper {
    fun getModDataDir(level: ServerLevel): File {
        val worldFolder = level.server.getWorldPath(LevelResource.ROOT).toFile()
        return File(worldFolder, RustedComputer.Companion.MODID)
    }

    fun readFile(level: ServerLevel, name: String): File {
        return File(getModDataDir(level), name)
    }
}