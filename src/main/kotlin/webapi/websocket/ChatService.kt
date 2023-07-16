package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.util.*

class ChatService(webSocket: WebSocket) : WebSocketService(webSocket), Listener {
    override fun start() {
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerChat(event: io.papermc.paper.event.player.AsyncChatEvent) {
        send(
            ChatMessage(
                Player(event.player.name, event.player.uniqueId),
                (event.message() as TextComponent).content()
            )
        )
    }

    private data class ChatMessage(val player: Player, val message: String) : Message("chat")

    private data class Player(val name: String, val uuid: UUID)
}