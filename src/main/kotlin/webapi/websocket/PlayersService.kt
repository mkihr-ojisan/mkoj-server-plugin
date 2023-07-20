package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.google.gson.annotations.SerializedName
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.geysermc.api.Geyser
import org.geysermc.floodgate.api.FloodgateApi
import org.geysermc.floodgate.api.player.FloodgatePlayer
import org.geysermc.geyser.api.GeyserApi
import java.util.*

class PlayersService(webSocket: WebSocket) : WebSocketService(webSocket), Listener {
    private val floodgateApi = FloodgateApi.getInstance()

    override fun start() {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            send(PlayerJoinMessage(getPlayer(player)))
        }
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        send(PlayerJoinMessage(getPlayer(event.player)))
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        send(PlayerQuitMessage(getPlayer(event.player)))
    }

    private fun getPlayer(player: org.bukkit.entity.Player): Player {
        val floodgatePlayer: FloodgatePlayer? = floodgateApi.getPlayer(player.uniqueId)

        return if (floodgatePlayer?.isLinked == false) {
            Player(floodgatePlayer.username, player.uniqueId, player.lastLogin)
        } else {
            Player(player.name, player.uniqueId, player.lastLogin)
        }

    }

    private data class PlayerJoinMessage(val player: Player) : Message("player_join")

    private data class PlayerQuitMessage(val player: Player) : Message("player_quit")

    private data class Player(val name: String, val uuid: UUID, val lastLogin: Long) {
        val type = if (Geyser.api().isBedrockPlayer(uuid)) PlayerType.Bedrock else PlayerType.Java
    }

    private enum class PlayerType {
        @SerializedName("java")
        Java,

        @SerializedName("bedrock")
        Bedrock
    }
}