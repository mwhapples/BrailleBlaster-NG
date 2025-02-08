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
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils
import org.mwhapples.jlouis.Louis
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Loads build properties file Jenkins inserts into the jar files before distributing in the maven
 * repository
 */
object VersionInfo {
    private val log = LoggerFactory.getLogger(VersionInfo::class.java)
    private val ALL_PROJECTS = listOf(Project.BB, Project.JLOUIS, Project.LIBLOUIS)

    @JvmStatic
    val versionsSimple: String
        get() = createSummaryString { obj: Project -> generateVersionsShort(obj) }
    private val LIBLOUIS_INSTANCE = Louis()
    private fun createSummaryString(generator: Generator): String {
        val result = StringBuilder()
        for (curProj in ALL_PROJECTS) {
            try {
                result.append(generator.generate(curProj))
                result.append(System.lineSeparator())
            } catch (e: Exception) {
                throw RuntimeException("Failed to load project " + curProj.name, e)
            }
        }
        result.append(javaVersion).append(System.lineSeparator()).append(oSVersion)
        return result.toString()
    }

    @JvmStatic
    val javaVersion: String
        get() = ("Java "
                + System.getProperty("java.version")
                + " "
                + (if (Platform.is64Bit()) "64-bit" else "32-bit")
                + " "
                + Locale.getDefault())

    @JvmStatic
    val oSVersion: String
        get() = SystemUtils.OS_NAME + " Version " + SystemUtils.OS_VERSION

    private fun generateVersionsShort(project: Project): String {
        var version = project.displayName + ": " + project.version
        val date = project.date
        if (date != null) {
            version += " built on " + project.date
        }
        return version
    }

    sealed class Project {
        data object BB : Project() {
            val isStableRelease: Boolean
                get() = '-' !in displayName
            override val versionDevFallback: String? by lazy {
                try {
                    // this should always work, BB can't be run from outside the BB or dist folder
                    val exec = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "HEAD"))
                    val bufferedReader = BufferedReader(InputStreamReader(exec.inputStream))
                    "local developer copy: " + bufferedReader.readLine()
                } catch (e: Exception) {
                    log.trace("Failed to get version", e)
                    null
                }
            }
        }

        data object JLOUIS : Project() {
            override val realVersion: String by lazy {
                val mvnProps = MavenProperties()
                val s = mvnProps.getProperty("jlouis.version")
                s ?: "No version information found"
            }
        }

        data object LIBLOUIS : Project() {
            override val buildDataStream: InputStream? by lazy {
                val name = "/" + Platform.RESOURCE_PREFIX + "/.build_data_" + name.lowercase(Locale.getDefault())
                VersionInfo::class.java.getResourceAsStream(name)
            }

            override val buildDataRevStream: InputStream? by lazy {
                val name =
                    "/" + Platform.RESOURCE_PREFIX + "/.build_data_" + name.lowercase(Locale.getDefault()) + ".rev"
                VersionInfo::class.java.getResourceAsStream(name)
            }

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
            val buildDataStream = buildDataStream
            if (buildDataStream != null) {
                val result = Properties()
                try {
                    result.load(buildDataStream)
                } catch (e: Exception) {
                    throw RuntimeException("Failed to load $this", e)
                }
                result
            } else null
        }
        open val realVersion: String by lazy {
            // load from Jenkins build properties or Mercurial on developer machines
            // load from
            (buildDataProperties?.getProperty("version") ?: versionDevFallback) ?: "No version information found"
        }
        var overrideVersion: String? = null
        val version: String
            get() = overrideVersion ?: realVersion
        val date: String? by lazy {
            buildDataProperties?.getProperty("date")
        }
        val versionWithRev: String by lazy {
            val revStream: InputStream? = buildDataRevDevStream ?: buildDataRevStream
            val suffix: String = if (revStream != null) {
                try {
                    " " + IOUtils.toString(revStream, StandardCharsets.UTF_8).trim { it <= ' ' }
                } catch (e: Exception) {
                    log.error("failed to read rev file", e)
                    " (failed to read rev file)"
                }
            } else {
                ""
            }
            version + suffix
        }


        protected open val versionDevFallback: String?
            get() = null
        open val buildDataStream: InputStream? by lazy {
            val name = "/.build_data_" + name.lowercase(Locale.getDefault())
            VersionInfo::class.java.getResourceAsStream(name)
        }
        open val buildDataRevStream: InputStream? by lazy {
            val name = "/.build_data_" + name.lowercase(Locale.getDefault()) + ".rev"
            VersionInfo::class.java.getResourceAsStream(name)
        }
        val buildDataRevDevStream: InputStream? by lazy {
            val name = "/.build_data_" + name.lowercase(Locale.getDefault()) + ".rev.dev"
            VersionInfo::class.java.getResourceAsStream(name)
        }
    }

    private fun interface Generator {
        @Throws(IOException::class)
        fun generate(project: Project): String?
    }

}
