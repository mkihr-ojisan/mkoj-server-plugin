package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.google.gson.annotations.SerializedName
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.geysermc.api.Geyser
import java.util.*

class PlayersService(webSocket: WebSocket) : WebSocketService(webSocket), Listener {
    override fun start() {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            send(
                PlayerJoinMessage(
                    Player(
                        player.name,
                        player.uniqueId
                    )
                )
            )
        }
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        send(PlayerJoinMessage(Player(event.player.name, event.player.uniqueId)))
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        send(PlayerQuitMessage(Player(event.player.name, event.player.uniqueId)))
    }

    private data class PlayerJoinMessage(val player: Player) : Message("player_join")

    private data class PlayerQuitMessage(val player: Player) : Message("player_quit")

    private data class Player(val name: String, val uuid: UUID) {
        val type = if (Geyser.api().isBedrockPlayer(uuid)) PlayerType.Bedrock else PlayerType.Java
    }

    private enum class PlayerType {
        @SerializedName("java")
        Java,

        @SerializedName("bedrock")
        Bedrock
    }
}