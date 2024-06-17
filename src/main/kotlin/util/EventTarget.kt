package com.mkihr_ojisan.mkoj_server_plugin.util

open class EventTarget<L : EventListener> {
    private val listeners = ArrayList<L>()

    fun addEventListener(listener: L) {
        listeners.add(listener)
    }

    fun removeEventListener(listener: L) {
        listeners.remove(listener)
    }

    fun dispatchEvent(event: Event) {
        listeners.forEach { it.dispatch(event) }
    }
}

open class Event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler

open class EventListener {
    private val events: Map<Class<*>, (Event) -> Any> =
            javaClass.methods
                    .filter { it.isAnnotationPresent(EventHandler::class.java) }
                    .associate { method ->
                        val eventType = method.parameterTypes[0]
                        eventType to { event: Event -> method.invoke(this, event) }
                    }

    fun dispatch(event: Event) {
        events[event.javaClass]?.invoke(event)
    }
}
