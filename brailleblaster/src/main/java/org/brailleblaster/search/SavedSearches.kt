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
package org.brailleblaster.search

/**
 * When we need changes to persist between instances of the search dialog, and
 * to read/write from settings files.  Hoping to factor this out so we can move
 * away from the .prop file without changing other code.
 */
object SavedSearches {
    @JvmField
    val findSavedSearches: ArrayList<String> = ArrayList()
    @JvmField
    val replaceSavedSearches: ArrayList<String> = ArrayList()
    private val memory = mutableListOf<Click>()
    @JvmField
    var lastSettings: SearchCriteria? = null

    @JvmStatic
    fun addToMemory(click: Click) {
        memory.add(click)
        click.settings.findString?.let { findString ->
            if (!findSavedSearches.contains(findString)) {
                findSavedSearches.add(findString)
            }
        }
        click.settings.replaceString?.let { replaceString ->
            if (!replaceSavedSearches.contains(replaceString)) {
                replaceSavedSearches.add(replaceString)
            }
        }
        lastSettings = click.settings
    }

    @JvmStatic
    val lastMemory: Click?
        get() = memory.lastOrNull()

    @JvmStatic
    fun findSize(): Int {
        return findSavedSearches.size
    }

    @JvmStatic
    fun replaceSize(): Int {
        return replaceSavedSearches.size
    }
}
