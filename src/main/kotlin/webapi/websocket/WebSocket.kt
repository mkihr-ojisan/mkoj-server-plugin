package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mkihr_ojisan.mkoj_server_plugin.ChatHistory
import com.mkihr_ojisan.mkoj_server_plugin.ChatHistoryEntry
import com.mkihr_ojisan.mkoj_server_plugin.ChatHistoryEntryType
import com.mkihr_ojisan.mkoj_server_plugin.ChatSender
import com.mkihr_ojisan.mkoj_server_plugin.ChatSenderType
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class WebSocket(private val headers: Map<String, List<String>>) : WebSocketAdapter() {
    private val subscribedServices = HashMap<String, WebSocketService>()

    private lateinit var remoteAddress: String

    override fun onWebSocketConnect(session: Session) {
        super.onWebSocketConnect(session)

        remoteAddress =
                headers["X-Forwarded-For"]?.firstOrNull() ?: session.remoteAddress.toString()
        MkojServerPlugin.getInstance().logger.info("WebSocket connected from $remoteAddress")
    }

    override fun onWebSocketText(message: String) {
        super.onWebSocketText(message)

        try {
            val json = JsonParser.parseString(message)

            when (json.asJsonObject.get("type").asString) {
                "subscribe" -> {
                    val serviceName = json.asJsonObject.get("service").asString

                    if (subscribedServices.containsKey(serviceName)) {
                        throw Exception("already subscribed")
                    }

                    val service =
                            WebSocketService.services[serviceName]?.constructors
                                    ?.first()
                                    ?.newInstance(this) as
                                    WebSocketService?
                                    ?: throw Exception("not found")
                    service.start()
                    subscribedServices[serviceName] = service
                }
                "unsubscribe" -> {
                    val serviceName = json.asJsonObject.get("service").asString

                    if (!subscribedServices.containsKey(serviceName)) {
                        throw Exception("not subscribed")
                    }

                    subscribedServices[serviceName]?.stop()
                    subscribedServices.remove(serviceName)
                }
                "say" -> {
                    val sayMessage = json.asJsonObject.get("message").asString
                    MkojServerPlugin.getInstance().server.onlinePlayers.forEach {
                        it.sendMessage("[Web] $sayMessage")
                    }
                    MkojServerPlugin.getInstance().logger.info("[Web] $sayMessage")
                    ChatHistory.addEntry(
                            ChatHistoryEntry(
                                    ChatHistoryEntryType.MESSAGE,
                                    ChatSender(ChatSenderType.WEB, null, null),
                                    sayMessage
                            )
                    )
                }
                "ping" -> {
                    session.remote.sendString("""{"type":"pong"}""")
                }
                else -> throw Exception("invalid type")
            }
        } catch (e: Exception) {
            session.remote.sendString("""{"error":"${e.message}"}""")
        }
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)

        subscribedServices.forEach { (_, service) -> service.stop() }

        MkojServerPlugin.getInstance().logger.info("WebSocket disconnected from $remoteAddress")
    }
}

abstract class WebSocketService(protected val webSocket: WebSocket) {
    companion object {
        val services =
                hashMapOf<String, Class<out WebSocketService>>(
                        "tps5s" to Tps5sService::class.java,
                        "players" to PlayersService::class.java,
                        "chat" to ChatService::class.java,
                        "weather" to WeatherService::class.java,
                        "time" to TimeService::class.java,
                )
    }

    abstract fun start()
    abstract fun stop()

    private val gson = Gson()
    protected fun send(data: Message) {
        if (webSocket.session.isOpen) {
            webSocket.session.remote.sendString(gson.toJson(data))
        }
    }

    protected open class Message(@Suppress("unused") val type: String)
}
