package com.mkihr_ojisan.mkoj_server_plugin.stats

import com.mkihr_ojisan.mkoj_server_plugin.util.runTaskTimer

object ServerStatistics {
    fun init() {
        runTaskTimer(0, 1, this::tick)
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

    // ロードされたチャンク数
    // 生成されたチャンク数
    // エンティティ数
    // dynmapのれんだーきゅー
    // CPU
    // memory
    // disk
}
