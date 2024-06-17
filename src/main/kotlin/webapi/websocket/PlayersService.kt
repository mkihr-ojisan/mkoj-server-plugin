package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.earth2me.essentials.Essentials
import com.google.gson.annotations.SerializedName
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import com.mkihr_ojisan.mkoj_server_plugin.util.runTaskTimer
import net.ess3.api.events.AfkStatusChangeEvent
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.scheduler.BukkitRunnable
import org.geysermc.floodgate.api.FloodgateApi
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

class PlayersService(webSocket: WebSocket) : WebSocketService(webSocket), Listener {
    private val floodgateApi = FloodgateApi.getInstance()
    private val essentials = Essentials.getPlugin(Essentials::class.java)

    private val updatedPlayers = HashSet<org.bukkit.entity.Player>()

    private lateinit var tickTask: BukkitRunnable

    override fun start() {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            send(PlayerJoinMessage(getPlayer(player)))
        }
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())

        runTaskTimer(0, 1, this::onTick)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
        tickTask.cancel()
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        send(PlayerJoinMessage(getPlayer(event.player)))
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        send(PlayerQuitMessage(getPlayer(event.player)))
    }

    @EventHandler
    fun onAfkStatusChange(event: AfkStatusChangeEvent) {
        if (event.cause != AfkStatusChangeEvent.Cause.JOIN && event.cause != AfkStatusChangeEvent.Cause.QUIT) {
            updatedPlayers.add(event.affected.base)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is org.bukkit.entity.Player) {
            updatedPlayers.add(entity)
        }
    }

    @EventHandler
    fun onEntityRegainHealthEvent(event: org.bukkit.event.entity.EntityRegainHealthEvent) {
        val entity = event.entity
        if (entity is org.bukkit.entity.Player) {
            updatedPlayers.add(entity)
        }
    }

    @EventHandler
    fun onPlayerArmorChange(event: PlayerArmorChangeEvent) {
        updatedPlayers.add(event.player)
    }

    @EventHandler
    fun onFoodLevelChange(event: org.bukkit.event.entity.FoodLevelChangeEvent) {
        val entity = event.entity
        if (entity is org.bukkit.entity.Player) {
            updatedPlayers.add(entity)
        }
    }

    private fun onTick() {
        if (updatedPlayers.isEmpty()) {
            return
        }

        val updatedPlayers: Array<org.bukkit.entity.Player> = updatedPlayers.toTypedArray()
        this.updatedPlayers.clear()

        object : BukkitRunnable() {
            override fun run() {
                updatedPlayers.forEach { player ->
                    send(PlayerUpdateMessage(getPlayer(player)))
                }
            }
        }.runTaskAsynchronously(MkojServerPlugin.getInstance())
    }

    private fun getPlayer(
        player: org.bukkit.entity.Player,
    ): Player {
        val floodgatePlayer: FloodgatePlayer? = floodgateApi.getPlayer(player.uniqueId)
        val essentialsUser = essentials.getUser(player)

        val name = if (floodgatePlayer?.isLinked == false) {
            floodgatePlayer.username
        } else {
            player.name
        }

        return Player(
            name,
            player.uniqueId,
            player.lastLogin,
            if (floodgatePlayer != null) PlayerType.Bedrock else PlayerType.Java,
            essentialsUser.isAfk,
            player.health,
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0,
            player.getAttribute(Attribute.GENERIC_ARMOR)?.value ?: 0.0,
            player.foodLevel
        )
    }

    private data class PlayerJoinMessage(val player: Player) : Message("player_join")

    private data class PlayerQuitMessage(val player: Player) : Message("player_quit")

    private data class PlayerUpdateMessage(val player: Player) : Message("player_update")

    private data class Player(
        val name: String,
        val uuid: UUID,
        val lastLogin: Long,
        val type: PlayerType,
        val afk: Boolean,
        val health: Double,
        val maxHealth: Double,
        val armor: Double,
        val food: Int
    )

    private enum class PlayerType {
        @SerializedName("java")
        Java,

        @SerializedName("bedrock")
        Bedrock
    }
}