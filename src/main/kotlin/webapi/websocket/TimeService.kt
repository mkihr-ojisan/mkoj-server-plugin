package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import org.bukkit.Bukkit
import java.util.*

class TimeService(webSocket: WebSocket) : WebSocketService(webSocket) {
    private val timer = Timer()

    override fun start() {
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (webSocket.session.isOpen) {
                        sendTime()
                    } else {
                        timer.cancel()
                    }
                }
            },
            0,
            1000
        )
    }

    override fun stop() {
        timer.cancel()
    }

    private fun sendTime() {
        val time = Bukkit.getWorlds()[0].time
        send(TimeMessage(time))
    }

    private data class TimeMessage(val time: Long) : Message("time")
}
