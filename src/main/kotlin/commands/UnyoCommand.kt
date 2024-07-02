package com.mkihr_ojisan.mkoj_server_plugin.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.TitlePart
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class UnyoCommand : CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val targetPlayers: List<Player> =
            if (args.isEmpty()) {
                if (sender is Player) {
                    listOf(sender)
                } else {
                    Bukkit.getOnlinePlayers().toList()
                }
            } else {
                if (args.contains("@a")) {
                    Bukkit.getOnlinePlayers().toList()
                } else {
                    args.map {
                        val player = Bukkit.getPlayerExact(it)
                        if (player == null) {
                            sender.sendMessage(
                                Component.text("指定したプレイヤーは存在しません", NamedTextColor.RED)
                            )
                            return false
                        }
                        player
                    }
                }
            }

        for (player in targetPlayers) {
            player.sendTitlePart(TitlePart.TITLE, Component.text("うにょーん"))
            player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f)
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        return mutableListOf("@a", *Bukkit.getOnlinePlayers().map { it.name }.toTypedArray())
    }
}
