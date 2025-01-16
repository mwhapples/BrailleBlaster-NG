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
package org.brailleblaster.utd.utils

import kotlin.math.max

class RomanNumeralConverter {

    fun convert(arabicNum: Int): String {
        return ROMAN_MAP.fold(StringBuilder() to arabicNum) { acc, entry ->
            acc.first.append(repeat(entry.first, acc.second / entry.second)) to acc.second % entry.second
        }.first.toString()
    }

    fun repeat(romanCode: String?, numOfTimes: Int): String? {
        if (romanCode == null) {
            return null
        }

        return romanCode.repeat(max(0.0, numOfTimes.toDouble()).toInt())
    } /*
	 * 
	 *
	 public int convert (String romanNumber) {
        int decimal = 0;
        int lastNumber = 0;
        String romanNumeral = romanNumber.toUpperCase();
        for (int x = romanNumeral.length() - 1; x >= 0 ; x--) {
            char convertToDecimal = romanNumeral.charAt(x);

            switch (convertToDecimal) {
                case 'M':
                    decimal = processDecimal(1000, lastNumber, decimal);
                    lastNumber = 1000;
                    break;
                case 'D':
                    decimal = processDecimal(500, lastNumber, decimal);
                    lastNumber = 500;
                    break;
                case 'C':
                    decimal = processDecimal(100, lastNumber, decimal);
                    lastNumber = 100;
                    break;
                case 'L':
                    decimal = processDecimal(50, lastNumber, decimal);
                    lastNumber = 50;
                    break;
                case 'X':
                    decimal = processDecimal(10, lastNumber, decimal);
                    lastNumber = 10;
                    break;
                case 'V':
                    decimal = processDecimal(5, lastNumber, decimal);
                    lastNumber = 5;
                    break;
                case 'I':
                    decimal = processDecimal(1, lastNumber, decimal);
                    lastNumber = 1;
                    break;
                case 'R':
                	break;
                default:
                	String temp = romanNumber;
                	if (isDecimal(convertToDecimal)) {
                		if (!isDecimal(romanNumber.charAt(0))) {
                			temp = romanNumber.substring(1, romanNumber.length());
                		}
                	}
                	return Integer.parseInt(temp);
            }
        }
        return decimal;
    }
	
	public boolean isDecimal(char input) {
		switch(input) {
			case '0':
				return true;
			case '1':
				return true;
			case '2':
				return true;
			case '3':
				return true;
			case '4':
				return true;
			case '5':
				return true;
			case '6':
				return true;
			case '7':
				return true;
			case '8':
				return true;
			case '9':
				return true;
		}
		
		return false;
	}
 
	 * 
	 * 
	 */

    companion object {

        private val ROMAN_MAP = listOf(
            "M" to 1000,
            "CM" to 900,
            "D" to 500,
            "CD" to 400,
            "C" to 100,
            "XC" to 90,
            "L" to 50,
            "XL" to 40,
            "X" to 10,
            "IX" to 9,
            "V" to 5,
            "IV" to 4,
            "I" to 1,
        )
    }
}
