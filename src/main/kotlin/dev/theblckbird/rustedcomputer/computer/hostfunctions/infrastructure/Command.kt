package dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure

import net.minecraft.server.level.ServerLevel

interface Command {
    fun run(level: ServerLevel): String
}