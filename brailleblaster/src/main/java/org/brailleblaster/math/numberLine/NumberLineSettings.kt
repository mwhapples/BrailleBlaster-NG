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

import org.brailleblaster.math.spatial.ISpatialMathSettings
import org.brailleblaster.math.spatial.MathText
import org.brailleblaster.math.spatial.SpatialMathEnum.Fill
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import org.brailleblaster.math.spatial.SpatialMathEnum.LabelPosition
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineOptions
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineType
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineViews
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.SpatialMathEnum.Translation
import org.brailleblaster.math.spatial.SpatialMathUtils.translate
import java.util.*

class NumberLineSettings : ISpatialMathSettings {
    var numUserDefinedIntervals: Int
        get() = userDefinedArray.size
        set(numUserDefinedIntervals) {
            if (userDefinedArray.size < numUserDefinedIntervals) {
                val difference = numUserDefinedIntervals - userDefinedArray.size
                userDefinedArray.addAll(0.until(difference).map { NumberLineInterval() })
            } else if (userDefinedArray.size > numUserDefinedIntervals) {
                val difference = userDefinedArray.size - numUserDefinedIntervals
                userDefinedArray = userDefinedArray.dropLast(difference).toMutableList()
            }
        }
    var intervalType: IntervalType = IntervalType.WHOLE
    var startLineCircle = Fill.NONE
    var endLineCircle = Fill.NONE
    var sectionType: NumberLineSection? = NumberLineSection.SEGMENT
    var isArrow = true
    var isStretch = false
    var isUseDecimal = false
    var isReduceFraction = true
    var isBeveledFraction = false
    var isRemoveLeadingZeros = false
    var isEndOverflow = false
    var isStartOverflow = false
    override var passage = Passage.NONE
    var type: NumberLineType? = NumberLineType.AUTOMATIC_MATH
    var view = NumberLineViews.AUTOMATIC_MATH
    var translationUserDefined: Translation = Translation.DIRECT
    var translationLabel: Translation = Translation.LITERARY
    var startSegmentCircle = Fill.FULL
    var endSegmentCircle = Fill.FULL
    var userDefinedArray: MutableList<NumberLineInterval> = 0.until(DEFAULT_USER_INTERVALS).map {
        NumberLineInterval(userText=MathText())
    }.toMutableList()
    var segmentStartInterval = 1
    var segmentEndInterval = 1
    var labelPosition: LabelPosition? = LabelPosition.BOTTOM
    var options: EnumSet<NumberLineOptions> = EnumSet.noneOf(NumberLineOptions::class.java)
    var stringParser = NumberLineStringParser()

    var printArray: List<String>
        get() = userDefinedArray.map { it.userText.print }
        set(value) {
            userDefinedArray.clear()
            for (i in value.indices) {
                val print = value[i]
                val braille = translate(translationUserDefined, print)
                userDefinedArray.add(
                    NumberLineInterval(userText=MathText(print=print, braille=braille))
                )
            }
        }

    val brailleArray: List<String>
        get() = userDefinedArray.map { it.userText.braille }

    companion object {
        private const val DEFAULT_USER_INTERVALS = 2
    }
}