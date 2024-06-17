package com.mkihr_ojisan.mkoj_server_plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class WelcomeMessage : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage(
                Component.empty()
                        .append(
                                Component.text(
                                        "======================================\n",
                                        NamedTextColor.GREEN
                                )
                        )
                        .append(
                                Component.text(
                                        "    mkoj serverへようこそ！",
                                        NamedTextColor.GREEN,
                                        TextDecoration.BOLD
                                )
                        )
                        .append(Component.text(" "))
                        .append(
                                Component.text(
                                                "地図",
                                                TextColor.color(0x6060FF),
                                                TextDecoration.UNDERLINED
                                        )
                                        .clickEvent(
                                                ClickEvent.openUrl(
                                                        "https://mc.mkihr-ojisan.com/maps/"
                                                )
                                        )
                        )
                        .append(Component.text(" "))
                        .append(
                                Component.text(
                                                "説明とか",
                                                TextColor.color(0x6060FF),
                                                TextDecoration.UNDERLINED
                                        )
                                        .clickEvent(
                                                ClickEvent.openUrl("https://mc.mkihr-ojisan.com/")
                                        )
                        )
                        .append(
                                Component.text(
                                        "\n======================================",
                                        NamedTextColor.GREEN
                                )
                        )
        )
    }
}
