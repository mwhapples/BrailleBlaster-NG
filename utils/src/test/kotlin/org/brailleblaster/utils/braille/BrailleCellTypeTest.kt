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
package org.brailleblaster.utils.braille

import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class BrailleCellTypeTest {
    @DataProvider(name = "multiplierProvider")
    fun multiplierProvider(): Iterator<Array<Any>> {
        val dataList: MutableList<Array<Any>> = ArrayList()
        for (i in 1..40) {
            dataList.add(arrayOf(i))
        }
        return dataList.iterator()
    }

    @Test(dataProvider = "multiplierProvider")
    fun widthRoundTest(numberOfCells: Int) {
        for (cell in BrailleCellType.entries) {
            val width = cell.cell.getWidthForCells(numberOfCells)
            Assert.assertEquals(cell.cellsFromWidth(width).toDouble(), numberOfCells.toDouble(), 0.01)
        }
    }

    @Test(dataProvider = "multiplierProvider")
    fun heightConvertRoundTest(numberOfLines: Int) {
        for (cell in BrailleCellType.entries) {
            val height = cell.cell.getHeightForLines(numberOfLines)
            Assert.assertEquals(cell.linesFromHeight(height).toDouble(), numberOfLines.toDouble(), 0.01)
        }
    }

    @DataProvider(name = "linesConvertProvider")
    fun linesConvertProvider(): Iterator<Array<Any>> {
        val dataList: MutableList<Array<Any>> = ArrayList()
        dataList.add(arrayOf(3, 25.38, BrailleCellType.SMALL_ENGLISH))
        return dataList.iterator()
    }

    @Test(dataProvider = "linesConvertProvider")
    fun linesFromHeight(lines: Int, height: Double, cell: BrailleCellType) {
        Assert.assertEquals(cell.linesFromHeight(height), lines)
    }

    @DataProvider(name = "cellsConvertProvider")
    fun cellsConvertProvider(): Iterator<Array<Any>> {
        val dataList: MutableList<Array<Any>> = ArrayList()
        dataList.add(arrayOf(12, 74.4, BrailleCellType.NLS))
        dataList.add(arrayOf(12, "74.4".toDouble(), BrailleCellType.NLS))
        return dataList.iterator()
    }

    @Test(dataProvider = "cellsConvertProvider")
    fun cellsFromWidth(cells: Int, width: Double, cell: BrailleCellType) {
        Assert.assertEquals(cell.cellsFromWidth(width), cells)
    }
}