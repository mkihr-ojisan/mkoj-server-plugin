package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.mkihr_ojisan.mkoj_server_plugin.ChatHistory
import com.mkihr_ojisan.mkoj_server_plugin.ChatHistoryEntry
import com.mkihr_ojisan.mkoj_server_plugin.NewEntryEvent
import com.mkihr_ojisan.mkoj_server_plugin.util.EventHandler
import com.mkihr_ojisan.mkoj_server_plugin.util.EventListener

class ChatService(webSocket: WebSocket) : WebSocketService(webSocket) {
    private val eventListener =
        object : EventListener() {
            @EventHandler
            fun onNewEntry(event: NewEntryEvent) {
                send(ChatMessage(event.entry))
            }
        }

    override fun start() {
        ChatHistory.getHistory().forEach { send(ChatMessage(it)) }
        ChatHistory.addEventListener(eventListener)
    }

    override fun stop() {
        ChatHistory.removeEventListener(eventListener)
    }

    private data class ChatMessage(val data: ChatHistoryEntry) : Message("chat")
}
