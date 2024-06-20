package com.mkihr_ojisan.mkoj_server_plugin

import com.mkihr_ojisan.mkoj_server_plugin.util.EventListener
import com.mkihr_ojisan.mkoj_server_plugin.util.EventTarget
import java.util.UUID
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ChatHistory : EventTarget<EventListener>(), Listener {
    private const val MAX_HISTORY_SIZE = 100
    private val history: ArrayDeque<ChatHistoryEntry> = ArrayDeque()

    fun init() {
        MkojServerPlugin.getInstance()
                .server
                .pluginManager
                .registerEvents(this, MkojServerPlugin.getInstance())
    }

    fun addEntry(entry: ChatHistoryEntry) {
        history.addLast(entry)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeFirst()
        }
        dispatchEvent(NewEntryEvent(entry))
    }

    fun getHistory(): List<ChatHistoryEntry> {
        return history.toList()
    }

    @EventHandler
    private fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        addEntry(
                ChatHistoryEntry(
                        ChatHistoryEntryType.PLAYER_JOIN,
                        ChatSender(ChatSenderType.PLAYER, event.player.name, event.player.uniqueId),
                        null
                )
        )
    }

    @EventHandler
    private fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        addEntry(
                ChatHistoryEntry(
                        ChatHistoryEntryType.PLAYER_QUIT,
                        ChatSender(ChatSenderType.PLAYER, event.player.name, event.player.uniqueId),
                        null
                )
        )
    }

    @EventHandler
    private fun onPlayerChat(event: io.papermc.paper.event.player.AsyncChatEvent) {
        addEntry(
                ChatHistoryEntry(
                        ChatHistoryEntryType.MESSAGE,
                        ChatSender(ChatSenderType.PLAYER, event.player.name, event.player.uniqueId),
                        (event.message() as TextComponent).content()
                )
        )
    }

    @EventHandler
    private fun onPlayerAdvancementDone(event: org.bukkit.event.player.PlayerAdvancementDoneEvent) {
        addEntry(
                ChatHistoryEntry(
                        ChatHistoryEntryType.PLAYER_ADVANCEMENT_DONE,
                        ChatSender(ChatSenderType.PLAYER, event.player.name, event.player.uniqueId),
                        null,
                        AdvancementInfo(
                                event.advancement.key.toString(),
                                AdvancementType.valueOf(
                                        event.advancement.display!!.frame().toString()
                                )
                        )
                )
        )
    }
}

enum class ChatSenderType {
    PLAYER,
    WEB,
}

data class ChatSender(val type: ChatSenderType, val name: String?, val uuid: UUID?)

enum class ChatHistoryEntryType {
    MESSAGE,
    PLAYER_JOIN,
    PLAYER_QUIT,
    PLAYER_ADVANCEMENT_DONE,
}

enum class AdvancementType {
    CHALLENGE,
    GOAL,
    TASK,
}

data class AdvancementInfo(val key: String, val type: AdvancementType)

data class ChatHistoryEntry(
        val type: ChatHistoryEntryType,
        val sender: ChatSender? = null,
        val message: String? = null,
        val advancement: AdvancementInfo? = null,
        val timestamp: Long = System.currentTimeMillis()
)

data class NewEntryEvent(val entry: ChatHistoryEntry) :
        com.mkihr_ojisan.mkoj_server_plugin.util.Event()
