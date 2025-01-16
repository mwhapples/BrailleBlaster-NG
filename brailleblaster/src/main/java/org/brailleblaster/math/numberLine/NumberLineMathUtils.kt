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
package org.brailleblaster.math.numberLine

import org.apache.commons.lang3.math.Fraction
import org.brailleblaster.math.spatial.SpatialMathEnum

object NumberLineMathUtils {
    fun getFractionString(numberLine: NumberLine, fraction: Fraction): String {
        var fraction = fraction
        val string: String
        if (numberLine.settings.intervalType == SpatialMathEnum.IntervalType.WHOLE) {
            string = fraction.toInt().toString()
        } else if (numberLine.settings.intervalType == SpatialMathEnum.IntervalType.DECIMAL) {
            if (numberLine.settings.isRemoveLeadingZeros) {
                string = fraction.toDouble().toString()
                val array = string.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (array.size < 2) {
                    return string
                }
                if (array[0].toInt() == 0) {
                    return "." + array[1]
                }
            } else {
                string = fraction.toDouble().toString()
            }
        } else if (numberLine.settings.intervalType == SpatialMathEnum.IntervalType.MIXED) {
            if (numberLine.settings.isReduceFraction) {
                string = if (fraction.properWhole == 0) {
                    fraction.reduce().toString()
                } else {
                    fraction.reduce().toProperString()
                }
            } else {
                fraction = unReduce(numberLine, fraction)
                string = if (fraction.properWhole == 0) {
                    fraction.toString()
                } else {
                    fraction.toProperString()
                }
            }
        } else {
            if (numberLine.settings.isReduceFraction) {
                string = fraction.reduce().toString()
            } else {
                fraction = unReduce(numberLine, fraction)
                string = fraction.toString()
            }
        }
        return string
    }

    /**
     * Un-reduce means to match the denominator to the interval denominator and
     * expand the numerator to match the equivalent
     *
     * @param numberLine
     * @param fraction
     * @return
     */
    fun unReduce(numberLine: NumberLine, fraction: Fraction): Fraction {
        val denominatorInterval = numberLine.numberLineText.interval.denominator.toInt()
        val thisDenominator = fraction.denominator
        return if (denominatorInterval == thisDenominator) {
            fraction
        } else {
            if (numberLine.settings.intervalType == SpatialMathEnum.IntervalType.IMPROPER) {
                val thisNumerator = fraction.numerator
                val factor = denominatorInterval / thisDenominator
                val newNumerator = thisNumerator * factor
                Fraction.getFraction(newNumerator, denominatorInterval)
            } else {
                val thisNumerator = fraction.properNumerator
                val factor = denominatorInterval / thisDenominator
                val newNumerator = thisNumerator * factor
                Fraction.getFraction(fraction.properWhole, newNumerator, denominatorInterval)
            }
        }
    }
}
