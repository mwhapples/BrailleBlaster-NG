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
package org.brailleblaster.embossers

import com.google.common.base.Preconditions
import com.google.gson.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.function.Supplier

class EmbosserConfigList(private val embosserConfigs: MutableList<EmbosserConfig> = mutableListOf(), @Transient private var embossersFile: File? = null) : MutableList<EmbosserConfig> by embosserConfigs {
    class JsonAdapter : JsonSerializer<EmbosserConfigList>, JsonDeserializer<EmbosserConfigList> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            src: JsonElement, srcType: Type, ctx: JsonDeserializationContext
        ): EmbosserConfigList {
            val obj = src.asJsonObject
            val embossers = EmbosserConfigList()
            if (obj.has(DEFAULT_NAME)) {
                embossers.defaultName = obj[DEFAULT_NAME].asString
            }
            if (obj.has(LAST_USED_NAME)) {
                embossers.lastUsedName = obj[LAST_USED_NAME].asString
            }
            embossers.isUseLastEmbosser = obj[USE_LAST].asBoolean
            for (e in obj[EMBOSSER_CONFIGS].asJsonArray) {
                try {
                    embossers.add(
                        Preconditions.checkNotNull(
                            ctx.deserialize(e, EmbosserConfig::class.java),
                            "Embosser profiles should never be null"
                        )
                    )
                } catch (ex: JsonParseException) {
                    // We don't add anything to the config list.
                    log.warn("Problem loading embosser profile from embossers file.", ex)
                } catch (ex: NullPointerException) {
                    log.error("Problem with embosser profile, ignoring it", ex)
                }
            }
            return embossers
        }

        override fun serialize(
            src: EmbosserConfigList, srcType: Type, ctx: JsonSerializationContext
        ): JsonElement {
            val obj = JsonObject()
            obj.addProperty(DEFAULT_NAME, src.defaultName)
            if (src.lastUsedName != null) {
                obj.addProperty(LAST_USED_NAME, src.lastUsedName)
            }
            obj.addProperty(USE_LAST, src.isUseLastEmbosser)
            val cfgs = JsonArray(src.embosserConfigs.size)
            for (e in src.embosserConfigs) {
                try {
                    cfgs.add(ctx.serialize(e))
                } catch (ex: Exception) {
                    // We do nothing because the config was not possible to serialise so we should not save
                    // it.
                    log.warn("Problem serialising embosser profile {}", e.name)
                    log.warn("The exception raised when serialising the embosser profile", ex)
                }
            }
            obj.add(EMBOSSER_CONFIGS, cfgs)
            return obj
        }

        companion object {
            private const val LAST_USED_NAME = "lastUsedName"
            private const val USE_LAST = "useLast"
            private const val DEFAULT_NAME = "defaultName"
            private const val EMBOSSER_CONFIGS = "embosserConfigs"
        }
    }

    private var defaultName: String? = null
    private var lastUsedName: String? = null
    var isUseLastEmbosser = true

    // When no default is set we resort to the first embosser.
    var defaultEmbosser: EmbosserConfig
        get() {
            if (embosserConfigs.isEmpty()) {
                throw NoSuchElementException()
            }
            // When no default is set we resort to the first embosser.
            return embosserConfigs.stream()
                .filter { e: EmbosserConfig -> e.name == defaultName }
                .findFirst()
                .orElseGet { embosserConfigs[0] }
        }
        set(embosser) {
            require(embosserConfigs.contains(embosser)) { "Specified embosser is not in embosser list" }
            defaultName = embosser.name
        }
    var lastUsedEmbosser: EmbosserConfig
        get() {
            if (embosserConfigs.isEmpty()) {
                throw NoSuchElementException()
            }
            return embosserConfigs.stream()
                .filter { e: EmbosserConfig -> e.name == lastUsedName }
                .findFirst()
                .orElseGet { embosserConfigs[0] }
        }
        set(embosser) {
            require(embosserConfigs.contains(embosser)) { "Specified embosser is not in embosser list" }
            lastUsedName = embosser.name
        }

    /**
     * Get the embosser as based upon user preferences.
     *
     *
     * This method will get the last used embosser should the preference for last used embosser
     * have been set, otherwise it will get the default embosser as defined by the user preferences.
     * This method delegates to the getLastUsedEmbosser() and getDefaultEmbosser() methods, so should
     * the embosser as defined by those preferences no longer exist then it will just return the first
     * embosser.
     *
     * @return The embosser as defined by the user's preferences.
     */
    val preferredEmbosser: EmbosserConfig
        get() = if (isUseLastEmbosser) lastUsedEmbosser else defaultEmbosser


    @Throws(IOException::class)
    fun saveEmbossers() {
        val embossersFile = this.embossersFile
        if (embossersFile != null) {
            saveEmbossers(embossersFile)
        } else {
            throw IllegalStateException(
                "The Embossers object has no default file name, use saveEmbossers(File) instead"
            )
        }
    }

    @Throws(IOException::class)
    fun saveEmbossers(embossersFile: File) {
        try {
            FileWriter(embossersFile).use { fw -> gson.toJson(this, fw) }
        } catch (_: JsonIOException) {
            throw IOException()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(EmbosserConfigList::class.java)
        private val gson = GsonBuilder()
            .registerTypeAdapter(EmbosserConfigList::class.java, JsonAdapter())
            .registerTypeAdapter(EmbosserConfig::class.java, EmbosserConfig.JsonAdapter())
            .create()

        fun loadEmbossers(
            embossersFile: File, s: Supplier<EmbosserConfigList>
        ): EmbosserConfigList {
            return try {
                loadEmbossers(embossersFile)
            } catch (_: IOException) {
                s.get().apply {
                    this.embossersFile = embossersFile
                }
            }
        }

        @Throws(IOException::class)
        fun loadEmbossers(embossersFile: File): EmbosserConfigList {
            var embossers: EmbosserConfigList?
            try {
                FileReader(embossersFile).use { fr -> embossers = gson.fromJson(fr, EmbosserConfigList::class.java) }
            } catch (_: JsonIOException) {
                throw IOException()
            }
            return embossers ?: EmbosserConfigList(embossersFile = embossersFile)
        }

    }
}
