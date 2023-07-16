package com.mkihr_ojisan.mkoj_server_plugin.webapi.websocket

import com.google.gson.annotations.SerializedName
import com.mkihr_ojisan.mkoj_server_plugin.MkojServerPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class WeatherService(webSocket: WebSocket) : WebSocketService(webSocket), Listener {
    override fun start() {
        sendWeather()
        Bukkit.getPluginManager().registerEvents(this, MkojServerPlugin.getInstance())
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onWeatherChange(event: org.bukkit.event.weather.WeatherChangeEvent) {
        sendWeather()
    }

    private fun sendWeather() {
        val world = Bukkit.getWorlds()[0]
        val (weather, duration) = when {
            world.isThundering -> Pair(Weather.Thunder, world.thunderDuration)
            world.hasStorm() -> Pair(Weather.Rain, world.weatherDuration)
            else -> Pair(Weather.Clear, world.weatherDuration)
        }
        send(WeatherMessage(weather, duration))
    }

    private data class WeatherMessage(val weather: Weather, val duration: Int) : Message("weather")
    private enum class Weather {
        @SerializedName("clear")
        Clear,
        @SerializedName("rain")
        Rain,
        @SerializedName("thunder")
        Thunder
    }
}