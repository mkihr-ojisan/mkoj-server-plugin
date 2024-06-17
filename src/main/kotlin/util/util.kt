package com.mkihr_ojisan.mkoj_server_plugin.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.bukkit.scheduler.BukkitRunnable

val gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

fun runTaskLaterAsynchronously(delay: Long, task: () -> Unit) {
    object : BukkitRunnable() {
                override fun run() {
                    task()
                }
            }
            .runTaskLaterAsynchronously(MkojServerPlugin.getInstance(), delay)
}

fun runTaskTimer(delay: Long, period: Long, task: () -> Unit) {
    object : BukkitRunnable() {
                override fun run() {
                    task()
                }
            }
            .runTaskTimer(MkojServerPlugin.getInstance(), delay, period)
}

fun runTaskAsynchronously(task: () -> Unit) {
    object : BukkitRunnable() {
                override fun run() {
                    task()
                }
            }
            .runTaskAsynchronously(MkojServerPlugin.getInstance())
}

fun runTask(task: () -> Unit) {
    object : BukkitRunnable() {
                override fun run() {
                    task()
                }
            }
            .runTask(MkojServerPlugin.getInstance())
}
