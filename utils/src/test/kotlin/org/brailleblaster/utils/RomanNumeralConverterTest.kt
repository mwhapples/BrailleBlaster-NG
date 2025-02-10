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

import org.testng.Assert
import org.testng.annotations.Test

class RomanNumeralConverterTest {
    @Test
    fun convertTest() {
        val converter = RomanNumeralConverter()

        Assert.assertEquals(converter.convert(1), "I")
        Assert.assertEquals(converter.convert(2), "II")
        Assert.assertEquals(converter.convert(4), "IV")
        Assert.assertEquals(converter.convert(5), "V")
        Assert.assertEquals(converter.convert(10), "X")
        Assert.assertEquals(converter.convert(15), "XV")
        Assert.assertEquals(converter.convert(40), "XL")
        Assert.assertEquals(converter.convert(45), "XLV")
        Assert.assertEquals(converter.convert(50), "L")
        Assert.assertEquals(converter.convert(55), "LV")
        Assert.assertEquals(converter.convert(90), "XC")
        Assert.assertEquals(converter.convert(95), "XCV")
        Assert.assertEquals(converter.convert(112), "CXII")
        Assert.assertEquals(converter.convert(450), "CDL")
        Assert.assertEquals(converter.convert(530), "DXXX")
        Assert.assertEquals(converter.convert(980), "CMLXXX")
        Assert.assertEquals(converter.convert(1000), "M")
        Assert.assertEquals(converter.convert(2000), "MM")
    }
}