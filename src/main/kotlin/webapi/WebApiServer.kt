package com.mkihr_ojisan.mkoj_server_plugin.webapi

import com.google.gson.JsonObject
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import com.mkihr_ojisan.mkoj_server_plugin.ServerInfo
import com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket.WebSocket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer
import java.net.URL
import java.time.Duration

object WebApiServer {
    private val server = Server()

    fun start() {
        val connector = ServerConnector(server)
        connector.port = 10000
        server.connectors = arrayOf(connector)

        val context = ServletContextHandler()
        context.addServlet(ServletHolder(WebApiServlet()), "/")
        server.handler = context

        JettyWebSocketServletContainerInitializer.configure(context) { _, wsContainer ->
            wsContainer.maxTextMessageSize = 65535
            wsContainer.idleTimeout = Duration.ofDays(1)
            wsContainer.addMapping("/ws") { req, _ -> WebSocket(req.headers) }
        }

        server.start()
    }

    fun stop() {
        server.stop()
    }
}

class WebApiServlet : jakarta.servlet.http.HttpServlet() {
    private val gson = com.google.gson.Gson()

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val url = URL(req.requestURL.toString())

        MkojServerPlugin.getInstance().logger.info("GET ${url.path}")

        try {
            val path = url.path.split("/")
            if (path.size < 2) throw ErrorResponse(404, "Not Found")

            when (path[1]) {
                "player" -> {
                    if (path.size < 3) throw ErrorResponse(404, "Not Found")

                    val uuid = path[2]
                    resp.contentType = "application/json"
                    resp.writer.write(gson.toJson(getPlayerStats(uuid)))
                }

                "server-info" -> {
                    resp.contentType = "application/json"
                    resp.writer.write(gson.toJson(JsonObject().apply {
                        addProperty("lowestSupportedVersion", ServerInfo.lowestSupportedVersion)
                        addProperty("highestSupportedVersion", ServerInfo.highestSupportedVersion)
                        addProperty("recommendedVersion", ServerInfo.recommendedVersion)
                    }))
                }

                else -> throw ErrorResponse(404, "Not Found")
            }
        } catch (e: ErrorResponse) {
            resp.status = e.status
            resp.writer.write(e.message ?: "")
        } catch (e: Exception) {
            resp.status = 500
            resp.writer.write("Internal Server Error")
            e.printStackTrace()
        }
    }
}

class ErrorResponse(val status: Int, message: String) : Exception(message)
