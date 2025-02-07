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
package org.brailleblaster.utils.localization

import com.ibm.icu.text.MessageFormat
import org.brailleblaster.utils.BBData
import org.slf4j.LoggerFactory
import java.net.URLClassLoader
import java.util.*
import java.util.Locale

/**
 * Provides i18n Strings when possible
 */
open class LocaleHandler(bundlePath: String, val locale: Locale) {
    private val log = LoggerFactory.getLogger(LocaleHandler::class.java)
    private val bundle: ResourceBundle?
    operator fun get(key: String): String {
        return try {
            bundle?.getString(key) ?: key
        } catch (e: MissingResourceException) {
            // Removed the exception due to volume of not-found keys
            log.warn("Locale key '{}' not found!", key)
            //			log.warn("Locale key not found!", e);
            key
        }
    }

    fun format(key: String, vararg arguments: Any?): String {
        val pattern = get(key)
        val mf = MessageFormat(pattern, locale)
        return mf.format(arguments, StringBuffer(), null).toString()
    }

    fun format(key: String, arguments: Map<String?, Any?>?): String {
        val pattern = get(key)
        val mf = MessageFormat(pattern, locale)
        return mf.format(arguments, StringBuffer(), null).toString()
    }

    init {
        try {
            // Do not depend on BBini which may not be created yet
            val langUrl = BBData.getBrailleblasterPath("programData", "lang").toURI().toURL()
            val loader: ClassLoader = URLClassLoader(arrayOf(langUrl))
            bundle = ResourceBundle.getBundle(bundlePath, locale, loader)
        } catch (ex: Exception) {
            throw RuntimeException("Cannot load locale", ex)
        }
    }
    companion object {
        @JvmStatic
        private var lhMap: MutableMap<Pair<String, Locale>, LocaleHandler> = mutableMapOf()
        @JvmStatic
        @JvmOverloads
        fun getInstance(bundleName: String, locale: Locale = Locale.getDefault()) =
            lhMap.getOrPut(bundleName to locale) { LocaleHandler(bundleName, locale) }
        @JvmStatic
        @JvmOverloads
        fun getDefault(locale: Locale = Locale.getDefault()) = getInstance("i18n", locale)
        @JvmStatic
        @JvmOverloads
        fun getBanaStyles(locale: Locale = Locale.getDefault()) = getInstance("bana-styles", locale)
        @JvmStatic
        fun clearLocaleCache() = lhMap.clear()
    }
}

