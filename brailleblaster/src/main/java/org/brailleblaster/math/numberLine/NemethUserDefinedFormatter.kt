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

import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addLabels
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addLine
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addPoints
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.dealWithBlanks
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.verticallyAlignLabelsAndPoints
import org.brailleblaster.math.spatial.MathFormattingException
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.settings.UTDManager.Companion.getCellsPerLine
import org.brailleblaster.wordprocessor.WPManager

object NemethUserDefinedFormatter : NemethNumberLineFormatter() {
    private fun formatPoints(numberLine: NumberLine, cellsPerLine: Int) {
        val points = ArrayList<NumberLinePoint>()
        for (i in numberLine.settings.userDefinedArray.indices) {
            points.add(
                NumberLinePoint(
                    mathText=numberLine.settings.userDefinedArray[i].userText)
            )
        }
        numberLine.points = points
        dealWithBlanks(numberLine)
        NumberLinePoint.makeColumnsEqualWidth(points)
        if (numberLine.settings.isStretch) {
            val addedCells = getStretchCells(numberLine, cellsPerLine)
            if (addedCells > 0) {
                for (i in numberLine.points.indices) {
                    numberLine.points[i]
                        .rightPadding += addedCells
                }
            }
        }
    }

    private fun getStretchCells(numberLine: NumberLine, cellsPerLine: Int): Int {
        formatLine(numberLine)
        val necessaryCells = NumberLineLine.getLineLength(numberLine.line)
        val leftoverCells = cellsPerLine - necessaryCells
        val intervals = numberLine.settings.userDefinedArray.size
        if (leftoverCells > intervals) {
            val addedCells = leftoverCells / intervals
            if (addedCells > 0) {
                return addedCells
            }
        }
        return 0
    }

    @Throws(MathFormattingException::class)
    fun format(numberLine: NumberLine): Boolean {
        determineOverflow(numberLine)
        val cellsPerLine = getCellsPerLine(WPManager.getInstance().controller)
        formatPoints(numberLine, cellsPerLine)
        verticallyAlignLabelsAndPoints(numberLine)
        formatLine(numberLine)
        if (numberLine.settings.sectionType == NumberLineSection.POINTS) {
            formatMultiplePoints(numberLine)
        } else if (numberLine.settings.sectionType == NumberLineSection.SEGMENT) {
            formatSegment(numberLine)
        }
        addLine(numberLine)
        if (!addPoints(numberLine)) {
            return false
        }
        addLabels(numberLine)
        return true
    }
}