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
package org.brailleblaster

import org.brailleblaster.util.FileUtils
import org.brailleblaster.util.PropertyFileManager
import org.brailleblaster.utils.OS
import org.brailleblaster.utils.os
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.bufferedReader
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.Path

class BBIniImpl(val bbDistPath: Path, bbUserPath: Path, propManager: PropertyFileManager?, debugArgs: List<String>) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    val nativeLibraryPath: Path = bbDistPath.resolve(Path("native", "lib"))
    val nativeLibrarySuffix = when(os) {
        OS.Windows -> ".dll"
        OS.Mac -> ".dylib"
        else -> ".so"
    }
    val programDataPath: Path = bbDistPath.resolve("programData")
    val userProgramDataPath: Path = bbUserPath.resolve("programData").also {
        if (!it.exists()) {
            try {
                it.createDirectories()
            } catch (e: IOException) {
                log.warn("Unable to create user program data directory", e)
            }
        }
    }
    val recentDocsPath: Path = userProgramDataPath.resolve("recent_documents.txt").also {
        FileUtils.create(it.toString())
    }
    val recentSaves: Path = userProgramDataPath.resolve(Path("autoSave", "recent_saves.txt")).also {
        FileUtils.create(it.toString())
    }
    val autoSavePath: Path = userProgramDataPath.resolve("autoSave").also {
        if (!it.exists()) {
            try {
                it.createDirectories()
            } catch (e: IOException) {
                log.warn("Unable to create autosave directory", e)
            }
        }
    }
    val autoSaveCrashPath: Path = autoSavePath.resolve("auto_save_error.txt")
    val aboutPropertiesPath: Path = programDataPath.resolve(Path("settings", "about.properties"))
    val releaseBuild: Boolean = Properties().let {
        try {
            it.load(aboutPropertiesPath.bufferedReader())
        } catch (e: IOException) {
            throw RuntimeException("Unable to load about file at $aboutPropertiesPath", e)
        }
        it.getProperty("releaseBuild", "false") == "true"
    }
    val helpDocsPath: Path = bbDistPath.resolve("docs")
    val logFilesPath: Path = bbUserPath.resolve("log").also {
        if (!it.exists()) {
            try {
                it.createDirectories()
            } catch (e: IOException) {
                log.warn("Unable to create log directory", e)
            }
        }
    }
    val propertyFileManager: PropertyFileManager = propManager ?: PropertyFileManager(userProgramDataPath.resolve(Path("settings", "user_settings.properties")).also {
        if (!it.exists()) {
            try {
                it.createFile()
            } catch (e: IOException) {
                throw RuntimeException("Cannot create user settings at $it", e)
            }
        }
    }.toString())
    val debugFilePath: Path? = debugArgs.firstOrNull()?.let {
        val p = Path(it).toAbsolutePath()
        if (p.exists()) {
            p
        } else {
            programDataPath.resolve(Path("testFiles", it)).also { dfp ->
                if (!dfp.exists()) {
                    throw RuntimeException("debug file $dfp does not exist")
                }
            }
        }
    }
    val debugSavePath: Path? = debugArgs.elementAtOrNull(1)?.let {
        val p = Path(it).toAbsolutePath()
        if (p.exists()) {
            p
        } else {
            programDataPath.resolve(Path("testFiles", it)).also { dfp ->
                if (!dfp.exists()) {
                    throw RuntimeException("debug file $dfp does not exist")
                }
            }
        }
    }
}