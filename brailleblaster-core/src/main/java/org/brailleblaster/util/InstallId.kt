/*
 * Copyright (C) 2025 American Printing House for the Blind
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.brailleblaster.util

import org.brailleblaster.BBIni
import org.brailleblaster.utils.PropertyFileManager
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException
import java.util.*
import kotlin.streams.asSequence

object InstallId {
    val macAddress: ByteArray? by lazy {
        try {
            val localHost = InetAddress.getLocalHost()
            val networkInterface: NetworkInterface? = NetworkInterface.getByInetAddress(localHost)
            networkInterface?.hardwareAddress?:NetworkInterface.networkInterfaces().asSequence().firstNotNullOfOrNull { it.hardwareAddress }
        } catch (e: UnknownHostException) {
            NetworkInterface.networkInterfaces().asSequence().firstNotNullOfOrNull { it.hardwareAddress }
        }
    }
    private fun generateUUID(): UUID {
        val nodeId = macAddress
        return if (nodeId != null) {
            val userName = System.getProperty("user.name").toByteArray(Charsets.UTF_8)
            UUID.nameUUIDFromBytes(nodeId + userName)
        } else {
            UUID.randomUUID()
        }
    }
    private fun generateAndSaveUUID(props: PropertyFileManager): UUID {
        val uuid = generateUUID()
        props.save("installUUID", uuid.toString())
        return uuid
    }
    val id: UUID by lazy {
        val recordedId: String? = BBIni.propertyFileManager.getProperty("installUUID")
        if (recordedId != null) {
            try {
                UUID.fromString(recordedId)
            } catch (e: IllegalArgumentException) {
                generateAndSaveUUID(BBIni.propertyFileManager)
            }
        } else {
            generateAndSaveUUID(BBIni.propertyFileManager)
        }
    }
}