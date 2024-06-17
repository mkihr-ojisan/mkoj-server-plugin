package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.mkihr_ojisan.mkoj_server_plugin.stats.ServerStatistics
import java.util.*

class Tps5sService(webSocket: WebSocket) : WebSocketService(webSocket) {
    private val timer = Timer()

    override fun start() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (webSocket.session.isOpen) {
                    send(Tps5sMessage(ServerStatistics.tps5s() ?: return))
                } else {
                    timer.cancel()
                }
            }
        }, 0, 1000)
    }

    override fun stop() {
        timer.cancel()
    }

    private data class Tps5sMessage(val tps: Double) : Message("tps5s")
}