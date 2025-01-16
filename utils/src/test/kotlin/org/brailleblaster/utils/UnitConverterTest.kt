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
package org.brailleblaster.utils

import org.brailleblaster.libembosser.spi.BrlCell
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class UnitConverterTest {
    @Test
    fun testCalculationsNonMetric() {
        val conversion = UnitConverter(false)
        Assert.assertEquals(5.0, conversion.calculateLinesFromHeight(2.0, BrlCell.NLS).toDouble(), 0.1)
        Assert.assertEquals(8.0, conversion.calculateCellsFromWidth(2.0, BrlCell.NLS).toDouble(), 0.1)
        Assert.assertEquals(0.5, conversion.calculateWidthFromCells(2, BrlCell.NLS), 0.1)
        Assert.assertEquals(0.5, conversion.calculateWidthFromCells(2, BrlCell.NLS), 0.1)
    }

    @Test
    fun testInchesToMMAndBack() {
        Assert.assertEquals(LengthUtils.inchesToMM(11.0), 279.4, 0.1)
        Assert.assertEquals(LengthUtils.mmToInches(300.0), 11.8, 0.1)
    }

    @DataProvider(name = "unitConverterProvider")
    fun unitConverterProvider(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(279.4, 279.4, true),
            arrayOf(300.0, 300.0, true),
            arrayOf(11.0, 279.4, false),
            arrayOf(11.8, 299.7, false)
        )
    }

    @Test(dataProvider = "unitConverterProvider")
    fun localUnitsToMM(localValue: Double, mmValue: Double, unitType: Boolean) {
        val converter = UnitConverter(unitType)
        Assert.assertEquals(converter.localUnitsToMM(localValue), mmValue, 0.1)
    }

    @Test(dataProvider = "unitConverterProvider")
    fun mmToLocalUnits(localValue: Double, mmValue: Double, unitType: Boolean) {
        val converter = UnitConverter(unitType)
        Assert.assertEquals(converter.mmToLocalUnits(mmValue), localValue, 0.1)
    }

    @Test
    fun testCalculationsMetric() {
        val conversion = UnitConverter(true)
        Assert.assertEquals(3.0, conversion.calculateLinesFromHeight(30.0, BrlCell.NLS).toDouble(), 0.1)
        Assert.assertEquals(4.0, conversion.calculateCellsFromWidth(30.0, BrlCell.NLS).toDouble(), 0.1)
        Assert.assertEquals(12.5, conversion.calculateWidthFromCells(2, BrlCell.NLS), 0.1)
        Assert.assertEquals(12.5, conversion.calculateWidthFromCells(2, BrlCell.NLS), 0.1)
    }

    @DataProvider(name = "numberOfCellsProvider")
    fun numberOfCellsProvider(): Array<Array<Any>> {
        val cellNLS = BrlCell.NLS
        return arrayOf(arrayOf(20, cellNLS), arrayOf(25, cellNLS), arrayOf(30, cellNLS), arrayOf(40, cellNLS))
    }

    @Test(dataProvider = "numberOfCellsProvider")
    fun testCellsToInchesAndBack(numOfCells: Int, cell: BrlCell?) {
        val unitConverter = UnitConverter(false)
        val widthInches = unitConverter.calculateWidthFromCells(numOfCells, cell!!)
        val resultCells = unitConverter.calculateCellsFromWidth(widthInches, cell)
        Assert.assertEquals(resultCells, numOfCells)
    }

    @DataProvider(name = "numberOfLinesProvider")
    fun numberOfLinesProvider(): Array<Array<Any>> {
        val cellNLS = BrlCell.NLS
        return arrayOf(arrayOf(20, cellNLS), arrayOf(25, cellNLS), arrayOf(30, cellNLS), arrayOf(40, cellNLS))
    }

    @Test(dataProvider = "numberOfCellsProvider")
    fun testLinesToInchesAndBack(numOfLines: Int, cell: BrlCell?) {
        val unitConverter = UnitConverter(false)
        val heightInches = unitConverter.calculateHeightFromLines(numOfLines, cell!!)
        val resultCells = unitConverter.calculateLinesFromHeight(heightInches, cell)
        Assert.assertEquals(resultCells, numOfLines)
    }
}