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

import com.google.gson.*
import org.brailleblaster.libembosser.EmbosserService
import org.brailleblaster.libembosser.spi.EmbossException
import org.brailleblaster.libembosser.spi.Embosser
import org.brailleblaster.libembosser.spi.EmbossingAttributeSet
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import java.io.InputStream
import java.lang.reflect.Type
import javax.print.DocFlavor
import javax.print.PrintService
import javax.print.PrintServiceLookup

class EmbosserConfig @JvmOverloads constructor(val name: String = "", var printerName: String? = null) {
    var embosserDriver: Embosser? = null
    private var embosserDriverId: String?
        get() = embosserDriver?.id
        set(value) {
            embosserDriver = EmbosserService.getInstance()
                .embosserStream
                .filter { e: Embosser -> e.id == value }
                .findFirst()
                .orElse(null)
        }

    fun setEmbosserDriver(manufacturer: String, model: String) {
        embosserDriver = EmbosserService.getInstance()
            .embosserStream
            .filter { e: Embosser -> e.manufacturer == manufacturer && e.model == model }
            .findFirst().orElse(null)
    }

    val isActive: Boolean
        get() = printService != null && embosserDriver != null
    private var printService: PrintService?
        get() = getPrinterForName(printerName)
        set(value) {
            printerName = value?.name
        }

    fun embossBrf(inputStream: InputStream?, attributes: EmbossingAttributeSet?): Boolean {
        val p = printService
        return if (p != null) {
            embossBrf(inputStream, attributes, p)
        } else {
            logger.warn("Embosser device not available")
            false
        }
    }

    fun embossBrf(
        inputStream: InputStream?, attributes: EmbossingAttributeSet?, ps: PrintService
    ): Boolean {
        val driver = embosserDriver
        requireNotNull(driver) { "Config must have an embosser driver set to be able to emboss" }
        try {
            driver.embossBrf(
                ps, inputStream!!, attributes!!
            )
        } catch (e: EmbossException) {
            logger.warn("Unable to emboss", e)
            return false
        }
        return true
    }

    fun embossPef(pef: Document?, attributes: EmbossingAttributeSet?): Boolean {
        val p = printService
        return if (p != null) {
            embossPef(pef, attributes, p)
        } else {
            logger.warn("Embosser device not available")
            false
        }
    }

    fun embossPef(pef: Document?, attributes: EmbossingAttributeSet?, ps: PrintService): Boolean {
        val driver = embosserDriver
        requireNotNull(driver) { "Config must have an embosser driver set to be able to emboss" }
        try {
            driver.embossPef(
                ps, pef!!, attributes!!
            )
        } catch (e: EmbossException) {
            logger.warn("Unable to emboss", e)
            return false
        }
        return true
    }

    class JsonAdapter : JsonDeserializer<EmbosserConfig>, JsonSerializer<EmbosserConfig> {
        @Throws(JsonParseException::class)
        override fun deserialize(src: JsonElement, srcType: Type, ctx: JsonDeserializationContext): EmbosserConfig {
            val obj = src.asJsonObject
            val name = obj[NAME].asString
            val printerName = obj[PRINTER_NAME].asString
            val embosser = EmbosserConfig(name, printerName)
            embosser.embosserDriverId = obj[EMBOSSER_DRIVER].asString
            val embosserDriver = embosser.embosserDriver
            if (embosserDriver != null && obj.has(EMBOSSER_OPTIONS)) {
                val options = embosserDriver.options
                val configOptions = obj[EMBOSSER_OPTIONS].asJsonObject
                val newOptions = options.mapValues { (k, v) ->
                    if (configOptions.has(k.id)) {
                        v.copy(configOptions[k.id].asString)
                    } else v
                }
                embosser.embosserDriver = embosserDriver.customize(newOptions)
            }
            return embosser
        }

        override fun serialize(src: EmbosserConfig, srcType: Type, ctx: JsonSerializationContext): JsonElement {
            val obj = JsonObject()
            obj.addProperty(NAME, src.name)
            obj.addProperty(PRINTER_NAME, src.printerName)
            obj.addProperty(EMBOSSER_DRIVER, src.embosserDriverId)
            val optionsObj = JsonObject()
            for ((k, v) in src.embosserDriver?.options ?: mapOf()) {
                optionsObj.addProperty(k.id, v.value)
            }
            obj.add(EMBOSSER_OPTIONS, optionsObj)
            return obj
        }

        companion object {
            private const val NAME = "name"
            private const val PRINTER_NAME = "printerName"
            private const val EMBOSSER_DRIVER = "embosserDriver"
            private const val EMBOSSER_OPTIONS = "embosserOptions"
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EmbosserConfig::class.java)
        private fun getPrinterForName(name: String?): PrintService? {
            val services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null)
            return services.firstOrNull { p: PrintService -> p.name == name }
        }
    }
}