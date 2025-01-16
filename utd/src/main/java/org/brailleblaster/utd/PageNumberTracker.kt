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
package org.brailleblaster.utd

import org.brailleblaster.utd.properties.PageNumberType
import org.brailleblaster.utd.utils.TextTranslator
import java.util.*
import kotlin.collections.HashMap

/**
 * Immutable class to hold the information for tracking page numbering.
 *
 * This class is made immutable so that a page can have an instance of this class and it can be certain that the page's numbering will not change. However by making this class immutable, it means that when you call a method which changes a value, such as calling a setter, then you will need to make use of the new returned instance.
 */
class PageNumberTracker {
    private val pageNumbers: MutableMap<PageNumberType, Int>

    /**
     * Get the current page number type.
     *
     * @return The type of the current page number.
     */
    lateinit var pageNumberType: PageNumberType
        private set

    /**
     * Get the padding cells of a page number.
     *
     * @return The number of cells separating the number from other Braille.
     */
    var padding: Int
        private set
    private var resetOnNextPage = false
    private var nextPageNumberType: PageNumberType? = null

    /**
     * Create a new PageNumberTracker for first normal page.
     */
    @JvmOverloads
    constructor(numberType: PageNumberType = PageNumberType.NORMAL) : this(1, numberType, false)

    /**
     * Create a PageNumberTracker for a specified page number and type.
     *
     * @param pageNumber The page number of this PageNumberTracker.
     * @param numberType The type of this page number.
     */
    constructor(pageNumber: Int, numberType: PageNumberType, isContinue: Boolean) {
        pageNumbers = EnumMap(PageNumberType::class.java)
        resetNumberCounters(pageNumber, numberType, isContinue)
        padding = 3
    }

    private fun resetNumberCounters(pageNumber: Int, numberType: PageNumberType, isContinue: Boolean) {
        for (nt in PageNumberType.entries) {
            if (!isContinue || nt != PageNumberType.NORMAL) {
                pageNumbers[nt] = 0
            }
        }
        pageNumbers[numberType] = pageNumber
        pageNumberType = numberType
    }

    fun resetNumberCounters(numberType: PageNumberType, isContinue: Boolean) {
        resetNumberCounters(1, numberType, isContinue)
    }

    fun resetNextPageNumberCounters(numberType: PageNumberType) {
        nextPageNumberType = numberType
    }

    /**
     * Create a copy of a PageNumberTracker.
     *
     * @param old The PageNumberTracker to copy.
     */
    constructor(old: PageNumberTracker) {
        pageNumbers = HashMap(old.pageNumbers)
        pageNumberType = old.pageNumberType
        padding = old.padding
    }

    var pageNumber: Int
        /**
         * Get the current page number.
         *
         * @return The current page number.
         */
        get() = pageNumbers.getOrDefault(pageNumberType, 0)
        set(page) {
            setPageNumber(pageNumberType, page)
        }

    fun getBraillePageNumber(engine: ITranslationEngine): String {
        return when (pageNumberType) {
            PageNumberType.P_PAGE, PageNumberType.T_PAGE, PageNumberType.NORMAL -> {
                TextTranslator.translateText(pageNumberType.getFormattedPageNumber(this.pageNumber), engine)
            }

        }
    }

    val braillePageNumber: String
        get() = pageNumberType.getFormattedPageNumber(pageNumber)

    /**
     * Create a new PageNumberTracker for the next page of the specified type.
     *
     * @param pageNumberType The type of the next page.
     * @return The new PageNumberTracker instance relating to the next page.
     */
    fun nextPage(pageNumberType: PageNumberType, isContinue: Boolean): PageNumberTracker {
        val result = PageNumberTracker(this)
        nextPageNumberType?.let {
            result.resetNumberCounters(1, it, isContinue)
            result.resetOnNextPage = false
        }
        nextPageNumberType = null
        val newPageNumber = pageNumber + 1
        result.pageNumbers[pageNumberType] = newPageNumber
        result.pageNumberType = pageNumberType
        return result
    }

    fun setPageNumberType(pageNumberType: PageNumberType, isContinue: Boolean) {
        if (!isContinue || pageNumberType != PageNumberType.NORMAL) {
            pageNumbers[pageNumberType] = 1
        }
        this.pageNumberType = pageNumberType
    }

    fun setPageNumberTypeContinue(pageNumberType: PageNumberType) {
        this.pageNumberType = pageNumberType
        var pageNumber = pageNumber
        if (pageNumber == 0) {
            pageNumber = 1
        }
        pageNumbers[pageNumberType] = pageNumber
    }

    /**
     * Set the number of padding cells for a page number.
     *
     * @param padding The number of cells which should separate the page number from other Braille on the line.
     * @return The new instance of PageNumberTracker with the new padding value set on it.
     */
    fun setPadding(padding: Int): PageNumberTracker {
        val result = PageNumberTracker(this)
        result.padding = padding
        return result
    }

    fun setPageNumber(pageNumberType: PageNumberType, page: Int) {
        pageNumbers[pageNumberType] = page
    }
}
