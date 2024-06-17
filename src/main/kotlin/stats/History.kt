package com.mkihr_ojisan.mkoj_server_plugin.stats

import com.mkihr_ojisan.mkoj_server_plugin.util.EventListener
import com.mkihr_ojisan.mkoj_server_plugin.util.EventTarget

class StatsHistory<T>(val size: Int, val interval: Int, val parent: StatsHistory<T>? = null) :
        EventTarget<StatsHistoryListener>() {
    private val history: ArrayDeque<T> = ArrayDeque(size)

    fun add(value: T) {
        history.removeFirst()
        history.addLast(value)
    }

    fun get(): List<T> {
        return history
    }
}

abstract class StatsHistoryListener : EventListener() {
    abstract fun onUpdate()
}
