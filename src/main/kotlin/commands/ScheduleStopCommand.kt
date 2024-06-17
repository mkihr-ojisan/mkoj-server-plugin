package com.mkihr_ojisan.mkoj_server_plugin.commands

import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class ScheduleStopCommand : CommandExecutor, Listener, TabCompleter {
    private var scheduled = false

    init {
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty() && !(args.size == 1 && args[0] == "cancel")) {
            return false
        }

        val cancel = args.getOrNull(0) == "cancel"

        if (cancel) {
            if (!scheduled) {
                sender.sendPlainMessage("スケジュールされていません")
                return true
            }
            scheduled = false
            sender.sendPlainMessage("スケジュールをキャンセルしました")
        } else {
            if (scheduled) {
                sender.sendPlainMessage("既にスケジュールされています")
                return true
            }

            if (Bukkit.getOnlinePlayers().isEmpty()) {
                Bukkit.shutdown()
                return true
            }

            scheduled = true
            sender.sendPlainMessage("スケジュールしました")
        }
        return true
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (scheduled && Bukkit.getOnlinePlayers().size == 1) {
            Bukkit.shutdown()
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return mutableListOf("cancel")
    }
}