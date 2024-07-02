package com.mkihr_ojisan.mkoj_server_plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

object Motd : Listener {
    fun init() {
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    private val MOTD: Lazy<Component> = lazy {
        Component.empty()
            .append(Component.text("むかおじサーバー", Style.style(TextDecoration.BOLD).color(NamedTextColor.WHITE)))
            .append(Component.newline())
            .append(Component.text("バージョン: ", NamedTextColor.GRAY))
            .append(Component.text(ServerInfo.recommendedVersion, NamedTextColor.WHITE))
            .append(Component.text(" (推奨)", NamedTextColor.WHITE))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(
                Component.text(
                    ServerInfo.lowestSupportedVersion,
                    NamedTextColor.GRAY
                )
            )
            .append(Component.text("〜", NamedTextColor.GRAY))
            .append(
                Component.text(
                    ServerInfo.highestSupportedVersion,
                    NamedTextColor.GRAY
                )
            )
            .append(Component.text(", Bedrock Edition ", NamedTextColor.GRAY))
    }

    @EventHandler
    private fun onServerListPing(event: ServerListPingEvent) {
        event.motd(MOTD.value)
    }
}