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
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class PageNumberTrackerTest {
    @Test
    fun defaultConstructor() {
        val pnt = PageNumberTracker()
        Assert.assertEquals(pnt.pageNumber, 1)
        //		assertEquals(pnt.getPageNumberType(), PageNumberType.UNSPECIFIED);
        Assert.assertEquals(pnt.pageNumberType, PageNumberType.NORMAL)
        Assert.assertEquals(pnt.padding, 3)
    }

    @Test(dataProvider = "startNumberProvider")
    fun constructWithStartNumber(startNumber: Int) {
        for (numberType in PageNumberType.entries) {
            val pnt = PageNumberTracker(startNumber, numberType, false)
            Assert.assertEquals(pnt.pageNumberType, numberType)
            Assert.assertEquals(pnt.pageNumber, startNumber)
            Assert.assertEquals(pnt.padding, 3)
        }
    }

    @Test
    fun nextPageNotSameInstance() {
        val pnt = PageNumberTracker()
        val result = pnt.nextPage(PageNumberType.NORMAL, false)
        Assert.assertNotSame(result, pnt)
    }

    @Test
    fun nextPageUsesNumberType() {
        val pnt = PageNumberTracker(PageNumberType.NORMAL)
        for (numberType in PageNumberType.entries) {
            val nextPage = pnt.nextPage(numberType, false)
            Assert.assertEquals(nextPage.pageNumberType, numberType)
            Assert.assertEquals(pnt.pageNumberType, PageNumberType.NORMAL)
        }
    }

    @DataProvider(name = "startNumberProvider")
    fun startNumberProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(1),
            arrayOf(2),
            arrayOf(3),
            arrayOf(5),
            arrayOf(7),
            arrayOf(10),
            arrayOf(23),
            arrayOf(57),
            arrayOf(64),
            arrayOf(102),
            arrayOf(157),
            arrayOf(205),
            arrayOf(1000)
        )
    }

    @Test(dataProvider = "startNumberProvider")
    fun nextPageNumberIncreases(startNumber: Int) {
        val expectedPageNumber = startNumber + 1
        for (numberType in PageNumberType.entries) {
            val pnt = PageNumberTracker(startNumber, numberType, false)
            val nextPage = pnt.nextPage(numberType, false)
            Assert.assertEquals(nextPage.pageNumber, expectedPageNumber)
        }
    }
}
