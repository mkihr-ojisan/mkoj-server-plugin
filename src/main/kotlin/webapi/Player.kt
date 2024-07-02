package com.mkihr_ojisan.mkoj_server_plugin.webapi

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

fun getPlayerStats(uuid: String): PlayerData {
    val player = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
    return PlayerData(player)
}

data class PlayerData(
    val name: String,
    val uuid: String,
    val lastDeathLocation: LocationData?,
    val bedSpawnLocation: LocationData?,
    val firstPlayed: Long,
    val lastLogin: Long,
    val lastSeen: Long,
    val isBanned: Boolean,
    val isWhitelisted: Boolean,
    val isOnline: Boolean,
    val isOp: Boolean,
    val stats: Map<String, Int>,
    val itemStats: Map<String, ItemStats>,
    val mobStats: Map<String, MobStats>,
    val onlinePlayerData: OnlinePlayerData?,
) {
    constructor(
        player: OfflinePlayer
    ) : this(
        name = player.name ?: throw ErrorResponse(404, "Player not found"),
        uuid = player.uniqueId.toString(),
        isBanned = player.isBanned,
        firstPlayed = player.firstPlayed,
        lastDeathLocation = player.lastDeathLocation?.let { LocationData(it) },
        bedSpawnLocation = player.bedSpawnLocation?.let { LocationData(it) },
        lastLogin = player.lastLogin,
        lastSeen = player.lastSeen,
        isWhitelisted = player.isWhitelisted,
        isOnline = player.isOnline,
        isOp = player.isOp,
        stats =
        Statistic.values().filter { it.type == Statistic.Type.UNTYPED }.associate {
            it.name to player.getStatistic(it)
        },
        itemStats =
        Material.values().filter { it.isItem }.associate {
            it.name to
                    ItemStats(
                        mined = player.getStatistic(Statistic.MINE_BLOCK, it),
                        broken = player.getStatistic(Statistic.BREAK_ITEM, it),
                        crafted = player.getStatistic(Statistic.CRAFT_ITEM, it),
                        used = player.getStatistic(Statistic.USE_ITEM, it),
                        pickedUp = player.getStatistic(Statistic.PICKUP, it),
                        dropped = player.getStatistic(Statistic.DROP, it)
                    )
        },
        mobStats =
        EntityType.values().filter { it.isAlive }.associate {
            it.name to
                    MobStats(
                        killed = player.getStatistic(Statistic.KILL_ENTITY, it),
                        killedBy =
                        player.getStatistic(Statistic.ENTITY_KILLED_BY, it)
                    )
        },
        onlinePlayerData = player.player?.let { OnlinePlayerData(it) }
    )
}

data class ItemStats(
    val mined: Int,
    val broken: Int,
    val crafted: Int,
    val used: Int,
    val pickedUp: Int,
    val dropped: Int
)

data class MobStats(val killed: Int, val killedBy: Int)

data class Advancement(
    val name: String,
    val isDone: Boolean,
    val awardedCriteria: Collection<String>,
    val remainingCriteria: Collection<String>
)

data class LocationData(
    val world: String?,
    val x: Double,
    val y: Double,
    val z: Double,
) {
    constructor(
        location: Location
    ) : this(world = location.world?.name, x = location.x, y = location.y, z = location.z)
}

data class OnlinePlayerData(
    val exp: Float,
    val level: Int,
    val locale: String,
    val ping: Int,
    val playerTime: Long,
    val playerTimeOffset: Long,
    val walkSpeed: Float,
    val isAllowingServerListings: Boolean,
    val isFlying: Boolean,
    val isSleepingIgnored: Boolean,
    val isSneaking: Boolean,
    val isSprinting: Boolean,
    val uniqueId: String,
    val advancements: Map<String, Advancement>?,
    val attributes: Map<String, Double?>?,
) {
    constructor(
        player: Player
    ) : this(
        exp = player.exp,
        level = player.level,
        locale = player.locale().toString(),
        ping = player.ping,
        playerTime = player.playerTime,
        playerTimeOffset = player.playerTimeOffset,
        walkSpeed = player.walkSpeed,
        isAllowingServerListings = player.isAllowingServerListings,
        isFlying = player.isFlying,
        isSleepingIgnored = player.isSleepingIgnored,
        isSneaking = player.isSneaking,
        isSprinting = player.isSprinting,
        uniqueId = player.uniqueId.toString(),
        advancements =
        Bukkit.getServer()
            .advancementIterator()
            .asSequence()
            .map { advancement ->
                advancement.key.key to
                        Advancement(
                            name = advancement.key.key,
                            isDone =
                            player.getAdvancementProgress(advancement)
                                .isDone,
                            awardedCriteria =
                            player.getAdvancementProgress(advancement)
                                .awardedCriteria,
                            remainingCriteria =
                            player.getAdvancementProgress(advancement)
                                .remainingCriteria
                        )
            }
            .toMap(),
        attributes = Attribute.values().associate { it.name to player.getAttribute(it)?.value }
    )
}
