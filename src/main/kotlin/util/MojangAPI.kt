package com.mkihr_ojisan.mkoj_server_plugin.util

import com.google.gson.JsonParser
import org.geysermc.floodgate.api.FloodgateApi
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object MojangAPI {
    private val client = HttpClient.newHttpClient()

    fun getJavaUUID(name: String): UUID? {
        val request =
            HttpRequest.newBuilder(URI("https://api.mojang.com/users/profiles/minecraft/$name"))
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val json = JsonParser.parseString(response.body())

        val errorMessage = json.asJsonObject["errorMessage"]?.asString
        if (errorMessage != null) {
            if (errorMessage.startsWith("Couldn't find any profile with name")) {
                return null
            } else {
                throw Exception(errorMessage)
            }
        }

        json.asJsonObject["id"].asString?.let {
            return UUID.fromString(
                it.replaceFirst(Regex("^(.{8})(.{4})(.{4})(.{4})(.{12})$"), "$1-$2-$3-$4-$5")
            )
        }

        throw Exception("Unknown error")
    }

    fun getBedrockUUID(name: String): UUID? {
        val request =
            HttpRequest.newBuilder(URI("https://api.geysermc.org/v2/xbox/xuid/$name")).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val json = JsonParser.parseString(response.body())

        val errorMessage = json.asJsonObject["message"]?.asString
        if (errorMessage != null) {
            if (errorMessage.startsWith("Unable to find user in our cache")) {
                return null
            } else {
                throw Exception(errorMessage)
            }
        }

        json.asJsonObject["xuid"]?.asLong?.let {
            return FloodgateApi.getInstance().createJavaPlayerId(it)
        }

        throw Exception("Unknown error")
    }
}
