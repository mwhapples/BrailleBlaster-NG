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
package org.brailleblaster.utd.utils;

import org.brailleblaster.utils.RomanNumeralConverter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RomanNumeralConverterTest {

    @Test
    public void convertTest() {
        RomanNumeralConverter converter = new RomanNumeralConverter();

        assertEquals(converter.convert(1), "I");
        assertEquals(converter.convert(2), "II");
        assertEquals(converter.convert(4), "IV");
        assertEquals(converter.convert(5), "V");
        assertEquals(converter.convert(10), "X");
        assertEquals(converter.convert(15), "XV");
        assertEquals(converter.convert(40), "XL");
        assertEquals(converter.convert(45), "XLV");
        assertEquals(converter.convert(50), "L");
        assertEquals(converter.convert(55), "LV");
        assertEquals(converter.convert(90), "XC");
        assertEquals(converter.convert(95), "XCV");
        assertEquals(converter.convert(112), "CXII");
        assertEquals(converter.convert(450), "CDL");
        assertEquals(converter.convert(530), "DXXX");
        assertEquals(converter.convert(980), "CMLXXX");
        assertEquals(converter.convert(1000), "M");
        assertEquals(converter.convert(2000), "MM");
    }
}
