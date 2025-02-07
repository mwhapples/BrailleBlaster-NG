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

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.search.SavedSearches.addToMemory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SearchController(private val m: Manager, private val click: Click) {
    private var viewCon: ViewControl? = null
    private var replaceAll: ReplaceAll? = null

    init {
        //System.out.println("SearchController init");
        clicks++
        logIt(clicks.toString())
    }

    fun replaceAll(): Int {
        replaceAll = ReplaceAll(m, click)
        return replaceAll!!.replaceAll(click)
    }

    fun recordSuccessfulClick() {
        val end = viewCon!!.currentViewState
        click.found = end
        addToMemory(click)
    }

    fun recordSuccessfulReplacement() {
        val end = viewCon!!.currentViewState
        click.replaced = end
        addToMemory(click)
    }

    val tableDoubles: Int
        get() = replaceAll!!.tableDoubles

    companion object {
        @JvmField
		val log: Logger = LoggerFactory.getLogger(SearchController::class.java)
        var clicks: Int = 0

        /**
         * Used for all FNR, including tests. If the tests are failing we can use
         * error, otherwise, use debug.
         *
         * @param s
         */
		@JvmStatic
		fun logIt(s: String?) {
            log.debug(s)
        }
    }
}
