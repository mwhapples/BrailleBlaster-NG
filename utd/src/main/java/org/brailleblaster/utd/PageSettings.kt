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

import jakarta.xml.bind.annotation.*
import org.brailleblaster.utd.utils.Page
import org.brailleblaster.utd.properties.PageNumberPosition
import org.brailleblaster.utd.utils.TextTranslator
import org.brailleblaster.utils.LengthUtils

@XmlRootElement(name = "pageSettings")
@XmlAccessorType(XmlAccessType.PROPERTY)
data class PageSettings(
        var paperWidth: Double = Page.DEFAULT_PAGE.getWidth(LengthUtils.Units.MILLIMETRES),
        var paperHeight: Double = Page.DEFAULT_PAGE.getHeight(LengthUtils.Units.MILLIMETRES),
        var leftMargin: Double = Page.DEFAULT_PAGE.getLeftMargin(LengthUtils.Units.MILLIMETRES),
        var rightMargin: Double = Page.DEFAULT_PAGE.getRightMargin(LengthUtils.Units.MILLIMETRES),
        var topMargin: Double = Page.DEFAULT_PAGE.getTopMargin(LengthUtils.Units.MILLIMETRES),
        var bottomMargin: Double = Page.DEFAULT_PAGE.getBottomMargin(LengthUtils.Units.MILLIMETRES),
        var printPages: Boolean = true,
        var braillePages: Boolean = true,
        var beginningPageNumber: Int = 1,
        var interpoint: Boolean = false,
        var indicatorChar: String = "-",
        private var _evenPrintPageNumberAt: PageNumberPosition = PageNumberPosition.TOP_RIGHT,
        private var _oddPrintPageNumberAt: PageNumberPosition = PageNumberPosition.TOP_RIGHT,
        private var _evenBraillePageNumberAt: PageNumberPosition = PageNumberPosition.BOTTOM_RIGHT,
        private var _oddBraillePageNumberAt: PageNumberPosition = PageNumberPosition.BOTTOM_RIGHT,
        var isPrintPageLetterIndicator: Boolean = false,
        var isPageTypeIndicator: Boolean = false,
        var isContinuePages: Boolean = false,
        var isPrintPageNumberRange: Boolean = true,
        var isPageNumberSeparateLine: Boolean = true,
        var isGuideWords: Boolean = true,
        var runningHead: String = "",
        var defaultTitle: String? = null,
        var defaultRunningHeadOption: RunningHeadOptions = RunningHeadOptions.NONE,
        private var pageLocationsUpdated: Boolean = true
) {

    private var braillePageNumberLocationSet: Boolean = false
    private var printPageNumberLocationSet: Boolean = false
    var begPageNum: Int
        get() = beginningPageNumber
        set(value) {
            beginningPageNumber = value
        }
    var evenPrintPageNumberAt
        get() = _evenPrintPageNumberAt
        set(value) {
            _evenPrintPageNumberAt = value
            printPageNumberLocationSet = true
            pageLocationsUpdated = true
        }
    var oddPrintPageNumberAt
        get() = _oddPrintPageNumberAt
        set(value) {
            _oddPrintPageNumberAt = value
            printPageNumberLocationSet = true
            pageLocationsUpdated = true
        }
    var evenBraillePageNumberAt
        get() = _evenBraillePageNumberAt
        set(value) {
            _evenBraillePageNumberAt = value
            braillePageNumberLocationSet = true
            pageLocationsUpdated = true
        }
    var oddBraillePageNumberAt
        get() = _oddBraillePageNumberAt
        set(value) {
            _oddBraillePageNumberAt = value
            braillePageNumberLocationSet = true
            pageLocationsUpdated = true
        }

    // Validate locations
    @get:XmlTransient
    var pageNumberLocations: List<PageNumberPosition> = mutableListOf()
        get() {
            if (pageLocationsUpdated) {
                field = mutableListOf(
                        evenPrintPageNumberAt,
                        oddPrintPageNumberAt,
                        evenBraillePageNumberAt,
                        oddBraillePageNumberAt)
            }
            return field
        }
        set(pageNumberLocations) {
            require(pageNumberLocations.size == 4) { "Requires exactly four page number positions." }

            // Validate locations
            val evenPrintPageNumberAt = if (pageNumberLocations[0] !== evenPrintPageNumberAt) pageNumberLocations[0] else evenPrintPageNumberAt
            val oddPrintPageNumberAt = if (pageNumberLocations[1] !== oddPrintPageNumberAt) pageNumberLocations[1] else oddPrintPageNumberAt
            val evenBraillePageNumberAt = if (pageNumberLocations[2] !== evenBraillePageNumberAt) pageNumberLocations[2] else evenBraillePageNumberAt
            val oddBraillePageNumberAt = if (pageNumberLocations[3] !== oddBraillePageNumberAt) pageNumberLocations[3] else oddBraillePageNumberAt
            require(!((evenPrintPageNumberAt != PageNumberPosition.NONE
                    && (evenBraillePageNumberAt == evenPrintPageNumberAt || oddBraillePageNumberAt == evenPrintPageNumberAt))
                    || (oddPrintPageNumberAt != PageNumberPosition.NONE
                    && (evenBraillePageNumberAt == oddPrintPageNumberAt || oddBraillePageNumberAt == oddPrintPageNumberAt))
                    || (evenBraillePageNumberAt != PageNumberPosition.NONE
                    && (evenPrintPageNumberAt == evenBraillePageNumberAt || oddPrintPageNumberAt == evenBraillePageNumberAt))
                    || (oddBraillePageNumberAt != PageNumberPosition.NONE
                    && (evenPrintPageNumberAt == oddBraillePageNumberAt || oddPrintPageNumberAt == oddBraillePageNumberAt))))
                    { "Braille and print page numbers cannot be placed in the same position." }
            field = pageNumberLocations
            this.evenPrintPageNumberAt = evenPrintPageNumberAt
            this.oddPrintPageNumberAt = oddPrintPageNumberAt
            this.evenBraillePageNumberAt = evenBraillePageNumberAt
            this.oddBraillePageNumberAt = oddBraillePageNumberAt
        }

    // Just ignore it as that element is no longer used and was not meant to be in there inj the
    // first place (IE. it has no meaning.
    // Due to files created with old UTD possibly containing pageNumberLocations elements the
    // following is for backwards compatability.
    @get:XmlElements(XmlElement(name = "pageNumberLocations"))
    @Deprecated(message = "Use evenBraillePageNumberAt, oddBraillePageNumberAt, evenPrintPageNumberAt and oddPrintPageNumberAt instead")
    var ignoredBackwardsCompat: List<Any>?
        get() = null
        set(_) {
            // Just ignore it as that element is no longer used and was not meant to be in there inj the
            // first place (IE. it has no meaning.
        }

    // Should no longer be used or saved
    @get:XmlElement(name = "braillePageNumberAt")
    @Deprecated(message = "Use evenBraillePageNumberAt and oddBraillePageNumberAt instead.")
    var braillePageNumberAtCompat: PageNumberPosition?
        get() = null
        set(pos) {
            // If the Braille page number positions have not been set then use this for even and odd pages.
            if (pos != null && !braillePageNumberLocationSet) {
                evenBraillePageNumberAt = pos
                oddBraillePageNumberAt = pos
            }
        }

    // No longer used and should not be saved
    @get:XmlElement(name = "printPageNumberAt")
    @Deprecated(message = "Use evenPrintPageNumberAt and oddPrintPageNumberAt instead.")
    var printPageNumberATCompat: PageNumberPosition?
        get() = null
        set(pos) {
            if (pos != null && !printPageNumberLocationSet) {
                evenPrintPageNumberAt = pos
                oddPrintPageNumberAt = pos
            }
        }

    val drawableHeight: Double
        get() = paperHeight - topMargin - bottomMargin
    val drawableWidth: Double
        get() = paperWidth - leftMargin - rightMargin

    fun setRunningHeadDefault(documentTitle: String) {
        runningHead = documentTitle
    }

    fun getLinesPerPage(brailleSettings: BrailleSettings): Int {
        return brailleSettings.cellType.getLinesForHeight(drawableHeight.toBigDecimal())
    }

    fun getCellsPerLine(brailleSettings: BrailleSettings): Int {
        return brailleSettings.cellType.getCellsForWidth(drawableWidth.toBigDecimal())
    }

    fun getRunningHead(engine: ITranslationEngine): String {
        return if (runningHead.isEmpty()) runningHead else TextTranslator.translateText(runningHead, engine)
    }
    enum class RunningHeadOptions {
        NONE, DEFAULT, TEXT
    }
}