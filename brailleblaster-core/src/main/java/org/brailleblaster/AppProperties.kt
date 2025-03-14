package org.brailleblaster

import java.util.Properties
import kotlin.io.path.reader

object AppProperties {
    private val properties: Properties = Properties().apply {
        runCatching {
            BBIni.bbDistPath.reader(Charsets.UTF_8)?.use { load(it) }
        }
    }
    val displayName = properties.getProperty("app.display-name", "BrailleBlaster")
    val version = properties.getProperty("app.version", "Unknown")
    val vendor = properties.getProperty("app.vendor", "Unknown")
}