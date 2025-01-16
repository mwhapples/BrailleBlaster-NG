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
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addLabels
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addLine
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.addPoints
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.dealWithBlanks
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.getSegmentBeginning
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.getSegmentEnding
import org.brailleblaster.math.numberLine.NumberLineFormatter.Companion.verticallyAlignLabelsAndPoints
import org.brailleblaster.math.numberLine.NumberLineLine.LineChar
import org.brailleblaster.math.numberLine.NumberLineSegmentPoint.Companion.getPointFromIndex
import org.brailleblaster.math.numberLine.NumberLineSegmentPoint.Companion.hasPoint
import org.brailleblaster.math.spatial.*
import org.brailleblaster.math.spatial.SpatialMathEnum.NumberLineSection
import org.brailleblaster.settings.UTDManager.Companion.getCellsPerLine
import org.brailleblaster.wordprocessor.WPManager
import org.mwhapples.jlouis.TranslationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.floor

open class NemethNumberLineFormatter {
  companion object {
    var log: Logger = LoggerFactory.getLogger(NemethNumberLineFormatter::class.java)

    fun formatSegment(numberLine: NumberLine) {
      val startInterval = numberLine.segment.startInterval
      val endInterval = numberLine.segment.endInterval
      var countOfIntervals = 0
      val line = Line()
      val array = numberLine.points
      if (numberLine.settings.isStartOverflow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.DOUBLE_BEGIN_ARROW.length))
      } else if (numberLine.settings.isArrow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.BEGIN_ARROW.length))
      }
      for (i in array.indices) {
        val mathText = array[i]
        countOfIntervals++
        if (i != 0) {
          line.elements
            .add(line.getWhitespaceSegment(mathText.leftPadding + mathText.leftDec.length))
        } else {
          line.elements.add(line.getWhitespaceSegment(1))
        }
        when (countOfIntervals) {
          startInterval -> {
            line.elements.add(line.getTextSegment(getSegmentBeginning(numberLine)))
          }

          endInterval -> {
            line.elements.add(line.getTextSegment(getSegmentEnding(numberLine)))
          }

          else -> {
            line.elements.add(line.getWhitespaceSegment(1))
          }
        }
        if (i != array.size - 1) {
          line.elements
            .add(line.getWhitespaceSegment(mathText.rightPadding + mathText.rightDec.length))
        } else {
          line.elements.add(line.getWhitespaceSegment(mathText.rightDec.length))
        }
      }
      if (numberLine.settings.isEndOverflow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.DOUBLE_END_ARROW.length))
      }
      if (numberLine.settings.isArrow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.END_ARROW.length))
      }
      numberLine.lines.add(line)
      if (!numberLine.segment.isPoint) {
        var inSegment = numberLine.settings.isStartOverflow
        countOfIntervals = 0
        for (i in numberLine.line.indices) {
          val lineSegment = numberLine.line[i]
          val c = lineSegment.type
          if (c == LineChar.INTERVAL) {
            countOfIntervals++
            if (countOfIntervals == startInterval) {
              inSegment = true
            } else if (countOfIntervals == endInterval) {
              inSegment = false
            }
          } else if (!LineChar.isArrow(c)) {
            if (inSegment) {
              lineSegment.type = LineChar.LINE_FILL
            }
          }
        }
      }
    }

    fun formatLine(numberLine: NumberLine) {
      val array = numberLine.points
      val lineChars = ArrayList<NumberLineLine>()
      if (numberLine.settings.isStartOverflow) {
        lineChars.add(NumberLineLine(LineChar.DOUBLE_ARROW_START))
      } else if (numberLine.settings.isArrow) {
        lineChars.add(NumberLineLine(LineChar.ARROW_START))
      }
      for (i in array.indices) {
        val mathText = array[i]
        if (i != 0) {
          lineChars.add(
            NumberLineLine(
              LineChar.LINE_EMPTY,
              mathText.leftPadding + mathText.leftDec.length
            )
          )
        } else {
          lineChars.add(NumberLineLine(LineChar.LINE_EMPTY))
        }
        lineChars.add(NumberLineLine(LineChar.INTERVAL))
        if (i != array.size - 1) {
          lineChars.add(
            NumberLineLine(
              LineChar.LINE_EMPTY,
              mathText.rightPadding + mathText.rightDec.length
            )
          )
        } else {
          lineChars.add(NumberLineLine(LineChar.LINE_EMPTY, numberLine.lastIntervalLength))
        }
      }
      if (numberLine.settings.isEndOverflow) {
        lineChars.add(NumberLineLine(LineChar.DOUBLE_ARROW_END))
      } else if (numberLine.settings.isArrow) {
        lineChars.add(NumberLineLine(LineChar.ARROW_END))
      }
      numberLine.line = lineChars
    }

    @Throws(MathFormattingException::class)
    fun formatPoints(numberLine: NumberLine, cellsPerLine: Int) {
      val points = ArrayList<NumberLinePoint>()
      val interval = numberLine.numberLineText.interval.fraction
      val lineStart = numberLine.numberLineText.lineStart.fraction
      val lineEnd = numberLine.numberLineText.lineEnd.fraction
      val totalUnits = lineEnd.subtract(lineStart).divideBy(interval).toDouble().toInt() + 1
      for (i in 0 until totalUnits) {
        val fraction: Fraction = if (i != 0) {
          val additionalUnits = interval.multiplyBy(Fraction.getFraction(i.toDouble()))
          lineStart.add(additionalUnits)
        } else {
          lineStart
        }
        val string = NumberLineMathUtils.getFractionString(numberLine, fraction)
        val braille = getBrailleFraction(numberLine, fraction)
        val mathText = MathText(print=string, braille=braille)
        points.add(
          NumberLinePoint(fraction=fraction, mathText=mathText,
            isMinus=fraction.toDouble() < 0)
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

    @Throws(MathFormattingException::class)
    fun getStretchCells(numberLine: NumberLine, cellsPerLine: Int): Int {
      formatLine(numberLine)
      val necessaryCells = NumberLineLine.getLineLength(numberLine.line)
      val leftoverCells = cellsPerLine - necessaryCells
      val intervals = floor(
        numberLine.numberLineText.lineEnd.fraction
          .subtract(numberLine.numberLineText.lineStart.fraction)
          .toDouble()
      ).toInt()
      if (leftoverCells > intervals) {
        val addedCells = leftoverCells / intervals
        if (addedCells > 0) {
          return addedCells
        }
      }
      return 0
    }

    private fun getBrailleFraction(numberLine: NumberLine, fraction: Fraction): String {
      var string: String
      var number = ""
      return when (numberLine.settings.intervalType) {
        SpatialMathEnum.IntervalType.WHOLE -> {
          string = fraction.toInt().toString()
          try {
            // log.error("Fraction string " + string);
            number = WPManager.getInstance().controller.document.engine.brailleTranslator
              .translateString(
                WPManager.getInstance().controller.document.engine
                  .brailleSettings.mathTextTable, string, 0
              )
          } catch (e: TranslationException) {
            e.printStackTrace()
          }
          number
        }

        SpatialMathEnum.IntervalType.DECIMAL -> {
          if (numberLine.settings.isRemoveLeadingZeros) {
            string = fraction.toDouble().toString()
            val array = string.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (array.size > 1) {
              if (array[0].toInt() == 0) {
                string = "." + array[1]
              }
            }
          } else {
            string = fraction.toDouble().toString()
          }
          try {
            log.error("Fraction string $string")
            number = WPManager.getInstance().controller.document.engine.brailleTranslator
              .translateString(
                WPManager.getInstance().controller.document.engine
                  .brailleSettings.mathTextTable, string, 0
              )
          } catch (e: TranslationException) {
            e.printStackTrace()
          }
          number
        }

        SpatialMathEnum.IntervalType.MIXED -> {
          makeMixedFractionString(
            numberLine, if (numberLine.settings.isReduceFraction) {
              fraction.reduce()
            } else {
              NumberLineMathUtils.unReduce(numberLine, fraction)
            }
          )
        }

        else -> {
          makeImproperFractionString(
            numberLine, if (numberLine.settings.isReduceFraction) {
              fraction.reduce()
            } else {
              NumberLineMathUtils.unReduce(numberLine, fraction)
            }
          )
        }
      }
    }

    private fun makeImproperFractionString(numberLine: NumberLine, fraction: Fraction): String {
      var string = ""
      val negative = fraction.toDouble() < 0
      try {
        val whole = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.properWhole).toString(), 0
          )
        val numerator = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.numerator).toString(),
            0
          )
        val denominator = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.denominator).toString(), 0
          )
        val fractionString: String = if (numberLine.settings.isBeveledFraction) {
          NemethTranslations.BEVELED_FRAC
        } else {
          NemethTranslations.FRAC
        }
        /*
        * There is the possibility that we can reduce the fraction to a whole number.
        * If we can, we should
        */
        var canReduceToWhole = false
        val wholeFrac = Fraction.getFraction(fraction.properWhole.toDouble())
        val numDenFrac = Fraction
          .getFraction(fraction.numerator.toString() + NemethTranslations.FRAC + fraction.denominator)
        if (wholeFrac.compareTo(numDenFrac) == 0) {
          canReduceToWhole = true
        }
        string = if (canReduceToWhole && numberLine.settings.isReduceFraction) {
          whole
        } else {
          (NemethTranslations.NEMETH_START_SIMPLE_FRAC + numerator + fractionString + denominator
              + NemethTranslations.NEMETH_END_SIMPLE_FRAC)
        }
      } catch (e: TranslationException) {
        e.printStackTrace()
      }
      return if (negative) NemethTranslations.MINUS + string else string
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
      } else if (numberLine.shouldFormatSegment()) {
        formatSegment(numberLine)
      }
      addLine(numberLine)
      if (!addPoints(numberLine)) {
        return false
      }
      addLabels(numberLine)
      return true
    }

    @JvmStatic
    protected fun formatMultiplePoints(numberLine: NumberLine) {
      val line = Line()
      val array = numberLine.points
      if (numberLine.settings.isStartOverflow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.DOUBLE_BEGIN_ARROW.length))
      } else if (numberLine.settings.isArrow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.BEGIN_ARROW.length))
      }
      for ((i, mathText) in array.withIndex()) {
        val interval = i + 1
        if (i != 0) {
          line.elements
            .add(line.getWhitespaceSegment(mathText.leftPadding + mathText.leftDec.length))
        } else {
          line.elements.add(line.getWhitespaceSegment(1))
        }
        if (hasPoint(numberLine, interval)) {
          line.elements.add(
            line.getTextSegment(
              getSegmentBeginning(
                getPointFromIndex(
                  numberLine,
                  interval
                )!!
              )
            )
          )
        } else {
          line.elements.add(line.getWhitespaceSegment(1))
        }
        if (i != array.size - 1) {
          line.elements
            .add(line.getWhitespaceSegment(mathText.rightPadding + mathText.rightDec.length))
        } else {
          line.elements.add(line.getWhitespaceSegment(mathText.rightDec.length))
        }
      }
      if (numberLine.settings.isEndOverflow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.DOUBLE_END_ARROW.length))
      }
      if (numberLine.settings.isArrow) {
        line.elements.add(line.getWhitespaceSegment(NemethTranslations.END_ARROW.length))
      }
      numberLine.lines.add(line)
    }

    @Throws(MathFormattingException::class)
    fun determineOverflow(numberLine: NumberLine) {
      if (numberLine.shouldFormatSegment()) {
        val endLineFraction = numberLine.numberLineText.lineEnd.fraction
        val startSegmentFraction = numberLine.numberLineText.segment.segmentStart.fraction
        val startLineFraction = numberLine.numberLineText.lineStart.fraction
        val endSegmentFraction = numberLine.numberLineText.segment.segmentEnd.fraction
        if (startSegmentFraction.subtract(startLineFraction).toDouble() < 0) {
          numberLine.settings.isStartOverflow = true
        }
        if (endSegmentFraction.subtract(endLineFraction).toDouble() > 0) {
          numberLine.settings.isEndOverflow = true
        }
      }
    }

    private fun makeMixedFractionString(numberLine: NumberLine, fraction: Fraction): String {
      var string = ""
      val negative = fraction.toDouble() < 0
      try {
        val whole = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.properWhole).toString(), 0
          )
        val numerator = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.properNumerator).toString(), 0
          )
        val denominator = WPManager.getInstance().controller.document.engine.brailleTranslator
          .translateString(
            WPManager.getInstance().controller.document.engine
              .brailleSettings.mathTextTable, abs(fraction.denominator).toString(), 0
          )
        val fractionString: String = if (numberLine.settings.isBeveledFraction) {
          NemethTranslations.BEVELED_FRAC
        } else {
          NemethTranslations.FRAC
        }
        /*
        * There is the possibility that we can reduce the fraction to a whole number.
        * If we can, we should
        */
        var canReduceToWhole = false
        val wholeFrac = Fraction.getFraction(fraction.properWhole.toDouble())
        val numDenFrac = Fraction
          .getFraction(fraction.numerator.toString() + NemethTranslations.FRAC + fraction.denominator)
        if (wholeFrac.compareTo(numDenFrac) == 0) {
          canReduceToWhole = true
        }
        string = if (canReduceToWhole && numberLine.settings.isReduceFraction) {
          whole
        } else if (wholeFrac.properWhole == 0) {
          (NemethTranslations.NEMETH_START_SIMPLE_FRAC + numerator + fractionString + denominator
              + NemethTranslations.NEMETH_END_SIMPLE_FRAC)
        } else {
          (whole + NemethTranslations.NEMETH_START_MIXED_FRAC + numerator + fractionString + denominator
              + NemethTranslations.NEMETH_END_MIXED_FRAC)
        }
      } catch (e: TranslationException) {
        e.printStackTrace()
      }
      return if (negative) NemethTranslations.MINUS + string else string
    }
  }
}