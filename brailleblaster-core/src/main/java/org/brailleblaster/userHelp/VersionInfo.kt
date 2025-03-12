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
package org.brailleblaster.userHelp

import com.sun.jna.Platform
import org.apache.commons.lang3.SystemUtils
import org.mwhapples.jlouis.Louis
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Loads build properties file Jenkins inserts into the jar files before distributing in the maven
 * repository
 */
object VersionInfo {
    private val log = LoggerFactory.getLogger(VersionInfo::class.java)
    private val ALL_PROJECTS = listOf(Project.BB, Project.LIBLOUIS)

    @JvmStatic
    val versionsSimple: String
        get() = createSummaryString { obj: Project -> generateVersionsShort(obj) }
    private val LIBLOUIS_INSTANCE = Louis()
    private fun createSummaryString(generator: Generator): String = (ALL_PROJECTS.map { curProj ->
        try {
            generator.generate(curProj)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load project " + curProj.name, e)
        }
    } + listOf(javaVersion, oSVersion)).joinToString(separator = System.lineSeparator())

    @JvmStatic
    val javaVersion: String
        get() = "Java ${System.getProperty("java.version")} ${if (Platform.is64Bit()) "64-bit" else "32-bit"} ${Locale.getDefault()}"

    @JvmStatic
    val oSVersion: String
        get() = "${SystemUtils.OS_NAME} Version ${SystemUtils.OS_VERSION}"

    private fun generateVersionsShort(project: Project): String {
        val date = project.date
        val builtOn = if (date != null) " built on $date" else ""
        return "${project.displayName}: ${project.version}${builtOn}"
    }

    sealed class Project {
        data object BB : Project() {
            override val realVersion: String
                get() = System.getProperty("app.version")
        }

        data object LIBLOUIS : Project() {
            override val buildDataStream: InputStream? by lazy {
                val name = "/" + Platform.RESOURCE_PREFIX + "/.build_data_" + name.lowercase(Locale.getDefault())
                VersionInfo::class.java.getResourceAsStream(name)
            }

            override val buildDataRevStream: InputStream?
                get() = VersionInfo::class.java.getResourceAsStream(
                    "/" + Platform.RESOURCE_PREFIX + "/.build_data_" + this.name.lowercase(
                        Locale.getDefault()
                    ) + ".rev"
                )

            override val realVersion: String by lazy {
                LIBLOUIS_INSTANCE.version
            }
        }

        val name: String = javaClass.simpleName
        val displayName: String by lazy {
            buildDataProperties?.getProperty("product") ?: (name.lowercase())
        }

        /** @see .getBuildDataProperties
         */
        private val buildDataProperties: Properties? by lazy {
            buildDataStream?.reader(Charsets.UTF_8)?.use {
                try {
                    Properties().apply {
                        load(it)
                    }
                } catch (e: Exception) {
                    throw RuntimeException("Failed to load $this", e)
                }
            }
        }
        open val realVersion: String by lazy {
            // load from Jenkins build properties or Mercurial on developer machines
            // load from
            buildDataProperties?.getProperty("version") ?: "No version information found"
        }
        val version: String
            get() = realVersion
        val date: String? by lazy {
            buildDataProperties?.getProperty("date")
        }
        val versionWithRev: String by lazy {
            val suffix: String = buildDataRevStream?.use {
                try {
                    " " + it.reader(Charsets.UTF_8).readText().trim { c -> c <= ' ' }
                } catch (e: Exception) {
                    log.error("failed to read rev file", e)
                    " (failed to read rev file)"
                }
            } ?: ""
            version + suffix
        }
        open val buildDataStream: InputStream?
            get() = VersionInfo::class.java.getResourceAsStream("/.build_data_${this.name.lowercase(Locale.getDefault())}")
        open val buildDataRevStream: InputStream?
            get() = VersionInfo::class.java.getResourceAsStream("/.build_data_${this.name.lowercase(Locale.getDefault())}.rev")
    }

    private fun interface Generator {
        @Throws(IOException::class)
        fun generate(project: Project): String?
    }

}
