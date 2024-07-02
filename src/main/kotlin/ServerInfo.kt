package com.mkihr_ojisan.mkoj_server_plugin

import com.viaversion.viaversion.api.Via

object ServerInfo {
    private val via = Via.getAPI()

    val lowestSupportedVersion: String = via.supportedProtocolVersions.first().includedVersions.first()
    val highestSupportedVersion: String = via.supportedProtocolVersions.last().includedVersions.last()
    val recommendedVersion: String = MkojServerPlugin.getInstance().server.minecraftVersion
}