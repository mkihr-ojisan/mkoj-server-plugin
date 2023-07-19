package com.mkihr_ojisan.mkoj_server_plugin.webapi

import com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket.WebSocket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer
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
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Hello, World!")
    }
}