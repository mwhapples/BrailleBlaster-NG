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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.function.Function

open class PropertyFileManager(val file: File, val prop: Properties = Properties()) {
    constructor(filePath: String) : this(File(filePath))
    init {
        if (file.canRead()) {
            FileInputStream(file).use {
                try {
                    prop.load(it)
                } catch (e: Exception) {
                    throw RuntimeException("Unable to load ${file.absolutePath}", e)
                }
            }
        }
    }

    fun save(property: String, value: String) {
        log.info("Setting {} to {}", property, value)
        prop.setProperty(property, value)
        save()
    }
    fun saveAsInt(property: String, value: Int) {
        save(property, value.toString())
    }

    fun saveAsBoolean(property: String, value: Boolean) {
        save(property, value.toString())
    }
    fun saveAsBooleanCompute(
        property: String?,
        value: Boolean,
        remappingFunction: Function<Boolean, Boolean>
    ) {
        prop.merge(
            property, value.toString()
        ) { oldValueRaw: Any, _: Any ->
            val oldValue = oldValueRaw.toString().toBoolean()
            val expected = remappingFunction.apply(oldValue)
            expected.toString()
        }
        log.info("Setting {} to {}", property, prop[property])
        save()
    }
    fun <E : Enum<E>> saveAsEnum(property: String, value: E) {
        save(property, value.toString())
    }
    fun getProperty(property: String): String? {
        return prop.getProperty(property)
    }
    fun getProperty(property: String, defaultValue: String): String {
        return prop.getProperty(property, defaultValue)
    }

    fun getPropertyAsBoolean(property: String, defaultValue: Boolean): Boolean {
        val value = getProperty(property)
        return if (value == null) defaultValue else java.lang.Boolean.parseBoolean(value)
    }

    fun getPropertyAsInt(property: String, defaultValue: Int): Int {
        val value = getProperty(property)
        return value?.toInt() ?: defaultValue
    }
    inline fun <reified E : Enum<E>> getPropertyAsEnumOrNull(property: String): E? {
        return getProperty(property)?.let { enumValueOf<E>(it) }
    }

    fun removeProperty(key: String): String? {
        log.info("Removing {}", key)
        return prop.remove(key) as String?
    }
    open fun save() {
        FileOutputStream(file).use {
            try {
                prop.store(it, null)
            } catch (e: Exception) {
                throw RuntimeException("Unable to save ${file.absolutePath}", e)
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PropertyFileManager::class.java)
    }
}