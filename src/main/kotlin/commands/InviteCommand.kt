package com.mkihr_ojisan.mkoj_server_plugin.commands

import com.mkihr_ojisan.mkoj_server_plugin.util.MojangAPI
import com.mkihr_ojisan.mkoj_server_plugin.util.runTask
import com.mkihr_ojisan.mkoj_server_plugin.util.runTaskAsynchronously
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class InviteCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }

        sender.sendMessage(Component.text("プレイヤーを検索しています..."))

        runTaskAsynchronously {
            for (playerName in args) {
                val javaUuid = MojangAPI.getJavaUUID(playerName)
                val bedrockUuid = MojangAPI.getBedrockUUID(playerName)
                if (javaUuid == null && bedrockUuid == null) {
                    sender.sendMessage(
                        Component.text(
                            "プレイヤー $playerName は存在しません。",
                            NamedTextColor.RED
                        )
                    )
                    continue
                }
                if (javaUuid != null) {
                    if (Bukkit.getOfflinePlayer(javaUuid).isWhitelisted) {
                        sender.sendMessage(Component.text("Java Edition のプレイヤー $playerName は既にホワイトリストに登録されています"))
                    } else {
                        runTask {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add $playerName")
                        }
                        sender.sendMessage(Component.text("Java Edition のプレイヤー $playerName をホワイトリストに登録しました"))
                    }
                }
                if (bedrockUuid != null) {
                    if (Bukkit.getOfflinePlayer(bedrockUuid).isWhitelisted) {
                        sender.sendMessage(Component.text("Bedrock Edition のプレイヤー $playerName は既にホワイトリストに登録されています"))
                    } else {
                        runTask {
                            Bukkit.dispatchCommand(
                                Bukkit.getConsoleSender(),
                                "fwhitelist add \"${playerName.replace("%20", " ")}\""
                            )
                        }
                        sender.sendMessage(Component.text("Bedrock Edition のプレイヤー $playerName をホワイトリストに登録しました"))
                    }
                }
            }
        }

        return true
    }
}