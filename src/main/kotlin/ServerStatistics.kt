package com.mkihr_ojisan.mkoj_server_plugin

import org.bukkit.scheduler.BukkitRunnable

object ServerStatistics {
    fun init() {
        object : BukkitRunnable() {
            override fun run() {
                tick()
            }
        }.runTaskTimer(MkojServerPlugin.getInstance(), 0, 1)
    }

    private const val TIMESTAMP_HISTORY_SIZE = 101
    private val timestamp = ArrayDeque<Long>(TIMESTAMP_HISTORY_SIZE)
    private fun tick() {
        if (timestamp.size >= TIMESTAMP_HISTORY_SIZE) {
            timestamp.removeFirst()
        }
        timestamp.addLast(System.currentTimeMillis())
    }

    fun tps5s(): Double? {
        if (timestamp.size != TIMESTAMP_HISTORY_SIZE) {
            return null
        }
        val last = timestamp.last()
        val first = timestamp.first()
        return (TIMESTAMP_HISTORY_SIZE - 1) / ((last - first) / 1000.0)
    }
}