package com.mkihr_ojisan.mkoj_server_plugin

import com.mkihr_ojisan.mkoj_server_plugin.commands.InviteCommand
import com.mkihr_ojisan.mkoj_server_plugin.commands.UnyoCommand
import com.mkihr_ojisan.mkoj_server_plugin.webapi.WebApiServer
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class MkojServerPlugin : JavaPlugin(), Listener {
    companion object {
        private var instance: MkojServerPlugin? = null

        fun getInstance(): MkojServerPlugin {
            return instance!!
        }
    }

    override fun onEnable() {
        instance = this

        Bukkit.getPluginManager().registerEvents(WelcomeMessage(), this)

        getCommand("unyo")!!.apply {
            val unyo = UnyoCommand()
            setExecutor(unyo)
            tabCompleter = unyo
        }
        getCommand("invite")!!.setExecutor(InviteCommand())

        WebApiServer.start()

        ServerStatistics.init()
    }

    override fun onDisable() {
        WebApiServer.stop()
    }
}