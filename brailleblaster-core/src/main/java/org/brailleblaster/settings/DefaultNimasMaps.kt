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
package org.brailleblaster.settings

import org.apache.commons.lang3.concurrent.AtomicSafeInitializer
import org.brailleblaster.BBIni
import org.brailleblaster.bbx.utd.BBXDynamicOptionStyleMap
import org.brailleblaster.bbx.utd.BBXStyleMap
import org.brailleblaster.utd.*
import org.brailleblaster.utd.config.StyleDefinitions
import org.brailleblaster.utd.config.UTDConfig
import java.util.function.Supplier

/**
 * Provides generic maps for unit testing and Run* classes without copy/pasting
 */
class DefaultNimasMaps {
    private val styleDefs: AtomicSafeInitializer<StyleDefinitions> =
        object : AtomicSafeInitializer<StyleDefinitions>() {
            override fun initialize(): StyleDefinitions {
                return UTDManager.loadStyleDefinitions(UTDManager.preferredFormatStandard)
            }
        }
    private val styleMap: AtomicSafeInitializer<StyleMap> = object : AtomicSafeInitializer<StyleMap>() {
        override fun initialize(): StyleMap {
            return UTDConfig.loadStyle(
                BBIni.loadAutoProgramDataFile("utd", "bbx.styleMap.xml"),
                styleDefs()
            )!!
        }
    }
    private val actionMap: AtomicSafeInitializer<ActionMap> = object : AtomicSafeInitializer<ActionMap>() {
        override fun initialize(): ActionMap {
            return UTDConfig.loadActions(
                BBIni.loadAutoProgramDataFile("utd", "bbx.actionMap.xml")
            )!!
        }
    }
    private val styleMultiMap: AtomicSafeInitializer<IStyleMap> = object : AtomicSafeInitializer<IStyleMap>() {
        override fun initialize(): IStyleMap {
            val newStyleMap = StyleMultiMap()
            newStyleMap.maps = mutableListOf(
                OverrideMap.generateOverrideStyleMap(styleDefs()),
                styleMap(),
                BBXStyleMap(styleDefs()),
                BBXDynamicOptionStyleMap(styleDefs(), Supplier<IStyleMap> {
                    try {
                        return@Supplier styleMap.get()
                    } catch (e: Exception) {
                        throw RuntimeException("wat", e)
                    }
                })
            )
            return newStyleMap
        }
    }

    fun styleDefs(): StyleDefinitions {
        return try {
            styleDefs.get()
        } catch (e: Exception) {
            throw RuntimeException("Failed to get value", e)
        }
    }

    fun styleMap(): StyleMap {
        return try {
            styleMap.get()
        } catch (e: Exception) {
            throw RuntimeException("Failed to get value", e)
        }
    }

    fun actionMap(): ActionMap {
        return try {
            actionMap.get()
        } catch (e: Exception) {
            throw RuntimeException("Failed to get value", e)
        }
    }

    /**
     * More BB like style map with the override map, BBX style map on disk, and dynamic BBX style map
     * @return
     */
    fun styleMultiMap(): IStyleMap {
        return try {
            styleMultiMap.get()
        } catch (e: Exception) {
            throw RuntimeException("Failed to get value", e)
        }
    }

}