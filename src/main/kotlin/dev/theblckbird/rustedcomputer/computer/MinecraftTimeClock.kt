package dev.theblckbird.rustedcomputer.computer

import net.minecraft.server.level.ServerLevel
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class MinecraftTimeClock(private val level: ServerLevel, private val zone: ZoneId = ZoneId.systemDefault()) : Clock() {
    private val tickInMilliseconds = 3600L
    private val minecraftEpoch = Instant.parse("2000-01-01T06:00:00Z")

    override fun getZone(): ZoneId = zone

    override fun withZone(zone: ZoneId): Clock = MinecraftTimeClock(level, zone)

    override fun instant(): Instant {
        val minecraftTime = level.dayTime
        val milliseconds = ticksToMilliseconds(minecraftTime)
        return minecraftEpoch.plusMillis(milliseconds)
    }

    fun ticksToMilliseconds(ticks: Long): Long {
        return ticks * tickInMilliseconds
    }
}