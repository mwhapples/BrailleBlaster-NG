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

import org.brailleblaster.utd.config.UTDConfig
import org.brailleblaster.utd.properties.PageNumberPosition
import org.brailleblaster.utd.testutils.UTDConfigUtils
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File
import java.io.IOException

class PageSettingsTest {
    private var page: PageSettings? = null
    private val engine: ITranslationEngine = UTDTranslationEngine()
    @BeforeMethod
    fun setPageSettings() {
        page = PageSettings()
    }

    @Test
    fun defaultConstructor() {
        Assert.assertEquals(page!!.paperWidth, 292.1, 0.01)
        Assert.assertEquals(page!!.paperHeight, 279.4, 0.01)
        Assert.assertEquals(page!!.leftMargin, 25.4, 0.01)
        Assert.assertEquals(page!!.rightMargin, 18.7, 0.01)
        Assert.assertEquals(page!!.topMargin, 12.7, 0.01)
        Assert.assertEquals(page!!.bottomMargin, 16.7, 0.01)
        Assert.assertFalse(page!!.interpoint)
        Assert.assertTrue(page!!.printPages)
        Assert.assertTrue(page!!.braillePages)
        Assert.assertEquals(page!!.beginningPageNumber, 1)
        Assert.assertEquals(page!!.evenPrintPageNumberAt, PageNumberPosition.TOP_RIGHT)
        Assert.assertEquals(page!!.oddPrintPageNumberAt, PageNumberPosition.TOP_RIGHT)
        Assert.assertEquals(page!!.evenBraillePageNumberAt, PageNumberPosition.BOTTOM_RIGHT)
        Assert.assertEquals(page!!.oddBraillePageNumberAt, PageNumberPosition.BOTTOM_RIGHT)
        Assert.assertFalse(page!!.isContinuePages)
        Assert.assertTrue(page!!.isPrintPageNumberRange)
        Assert.assertTrue(page!!.isPageNumberSeparateLine)
        Assert.assertEquals(page!!.getRunningHead(engine), "")
        Assert.assertTrue(page!!.indicatorChar == "-")
    }

    @Test
    @Throws(IOException::class)
    fun save() {
        val pageSettings = PageSettings()
        Assert.assertNotSame(pageSettings.leftMargin, 5.5, "Defaults changed, update test")
        pageSettings.leftMargin = 5.5
        val tempOutput = File.createTempFile("utdEngine", "test")
        UTDConfig.savePageSettings(tempOutput, pageSettings)
        UTDConfigUtils.compareOutputToSaved(tempOutput, UTDConfigUtils.TEST_PAGE_SETTINGS_FILE)
    }

    @Test
    @Throws(Exception::class)
    fun load() {
        val pageSettings = UTDConfig.loadPageSettings(UTDConfigUtils.TEST_PAGE_SETTINGS_FILE)
        Assert.assertEquals(pageSettings.leftMargin, 5.5)
    }

    @Test
    fun testSetters() {
        val setPaperHeight = 10.0
        page!!.paperHeight = setPaperHeight
        val getPaperHeight = page!!.paperHeight
        Assert.assertEquals(getPaperHeight, setPaperHeight)
        val setLeftMargin = 2.0
        page!!.leftMargin = setLeftMargin
        val getLeftMargin = page!!.leftMargin
        Assert.assertEquals(getLeftMargin, setLeftMargin)
        val setRightMargin = 2.0
        page!!.rightMargin = setRightMargin
        val getRightMargin = page!!.rightMargin
        Assert.assertEquals(getRightMargin, setRightMargin)
        val setTopMargin = 2.0
        page!!.topMargin = setTopMargin
        val getTopMargin = page!!.topMargin
        Assert.assertEquals(getTopMargin, setTopMargin)
        val setBottomMargin = 2.0
        page!!.bottomMargin = setBottomMargin
        val getBottomMargin = page!!.bottomMargin
        Assert.assertEquals(getBottomMargin, setBottomMargin)
        val setInterpoint = true
        page!!.interpoint = setInterpoint
        val getInterpoint = page!!.interpoint
        Assert.assertEquals(getInterpoint, setInterpoint)
        val setPrintPages = false
        page!!.printPages = setPrintPages
        val getPrintPages = page!!.printPages
        Assert.assertEquals(getPrintPages, setPrintPages)
        val setBraillePages = false
        page!!.braillePages = setBraillePages
        val getBraillePages = page!!.braillePages
        Assert.assertEquals(getBraillePages, setBraillePages)
        val setBegPageNum = 2
        page!!.beginningPageNumber = setBegPageNum
        val getBegPageNum = page!!.beginningPageNumber
        Assert.assertEquals(getBegPageNum, setBegPageNum)
        val setEvenPrintPageNumberAt = PageNumberPosition.BOTTOM_LEFT
        page!!.evenPrintPageNumberAt = setEvenPrintPageNumberAt
        val getEvenPrintPageNumberAt = page!!.evenPrintPageNumberAt
        Assert.assertEquals(getEvenPrintPageNumberAt, setEvenPrintPageNumberAt)
        val setOddPrintPageNumberAt = PageNumberPosition.BOTTOM_LEFT
        page!!.oddPrintPageNumberAt = setOddPrintPageNumberAt
        val getOddPrintPageNumberAt = page!!.oddPrintPageNumberAt
        Assert.assertEquals(getOddPrintPageNumberAt, setOddPrintPageNumberAt)

        // Make sure we do not try and set the Braille page number location to the same as the print
        // page number position.
        val setEvenBraillePageNumberAt = PageNumberPosition.BOTTOM_RIGHT
        page!!.evenBraillePageNumberAt = setEvenBraillePageNumberAt
        val getEvenBraillePageNumberAt = page!!.evenBraillePageNumberAt
        Assert.assertEquals(getEvenBraillePageNumberAt, setEvenBraillePageNumberAt)
        val setOddBraillePageNumberAt = PageNumberPosition.BOTTOM_RIGHT
        page!!.oddBraillePageNumberAt = setOddBraillePageNumberAt
        val getOddBraillePageNumberAt = page!!.oddBraillePageNumberAt
        Assert.assertEquals(getOddBraillePageNumberAt, setOddBraillePageNumberAt)
        val setContinuePages = true
        page!!.isContinuePages = setContinuePages
        val getContinuePages = page!!.isContinuePages
        Assert.assertEquals(getContinuePages, setContinuePages)
        val setPrintPageNumberRange = true
        page!!.isPrintPageNumberRange = setPrintPageNumberRange
        val getPrintPageNumberRange = page!!.isPrintPageNumberRange
        Assert.assertEquals(getPrintPageNumberRange, setPrintPageNumberRange)
        val setPageNumberSeparateLine = false
        page!!.isPageNumberSeparateLine = setPageNumberSeparateLine
        val getPageNumberSeparateLine = page!!.isPageNumberSeparateLine
        Assert.assertEquals(getPageNumberSeparateLine, setPageNumberSeparateLine)

        //		String documentTitle = "Some text";
        //		TextTranslator translator = new TextTranslator();
        //		String brlTitle = translator.translateText(documentTitle, new UTDTranslationEngine());
        //		page.setRunningHead(documentTitle);
        //		String getTitle = page.getRunningHead();
        //		assertEquals(getTitle, brlTitle);
        val indicatorChar = "="
        page!!.indicatorChar = indicatorChar
        Assert.assertTrue(page!!.indicatorChar == indicatorChar)
    }

    @Test(
        expectedExceptions = [IllegalArgumentException::class],
        expectedExceptionsMessageRegExp = "Braille and print page numbers cannot be placed in the same position.*"
    )
    fun preventEvenBrailleAndPrintPageNumbersInSamePosition() {
        val settings = PageSettings()
        val pageNumberLocations: MutableList<PageNumberPosition> = ArrayList()
        pageNumberLocations.add(PageNumberPosition.TOP_LEFT)
        pageNumberLocations.add(PageNumberPosition.TOP_LEFT)
        pageNumberLocations.add(PageNumberPosition.TOP_LEFT)
        pageNumberLocations.add(PageNumberPosition.TOP_LEFT)
        settings.pageNumberLocations = pageNumberLocations
    }

    @Test(
        expectedExceptions = [IllegalArgumentException::class],
        expectedExceptionsMessageRegExp = "Requires exactly four page number positions.*"
    )
    fun preventOddBrailleAndPrintPageNumbersInSamePosition() {
        val settings = PageSettings()
        val pageNumberLocations: MutableList<PageNumberPosition> = ArrayList()
        pageNumberLocations.add(PageNumberPosition.TOP_LEFT)
        settings.pageNumberLocations = pageNumberLocations
    }

    @get:Test
    val drawableHeight: Unit
        get() {
            val pageSettings = PageSettings()
            pageSettings.paperHeight = 300.0
            pageSettings.topMargin = 20.5
            pageSettings.bottomMargin = 24.5
            Assert.assertEquals(pageSettings.drawableHeight, 255.0, 0.1)
        }

    @get:Test
    val drawableWidth: Unit
        get() {
            val pageSettings = PageSettings()
            pageSettings.paperWidth = 250.0
            pageSettings.leftMargin = 10.0
            pageSettings.rightMargin = 7.5
            Assert.assertEquals(pageSettings.drawableWidth, 232.5, 0.1)
        }
}