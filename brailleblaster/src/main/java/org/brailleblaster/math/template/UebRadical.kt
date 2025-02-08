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
package org.brailleblaster.math.template

import org.brailleblaster.math.spatial.Line
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage
import org.brailleblaster.math.spatial.UebTranslations
import org.brailleblaster.math.template.Template.Companion.hasRemainder
import org.brailleblaster.math.template.Template.Companion.operandsBlank
import org.brailleblaster.math.template.TemplateNumber.TemplateNumberBuilder

class UebRadical {
  private val templateLine = ArrayList<TemplateNumber>()
  private var longestLine = 0
  private val whitespaceBetweenDivisorAndRadical = 1
  private var whitespaceBetweenRadicalAndDividend = 1
  private var verticalRadical: String? = null

  fun format(template: Template): Template {
    if (operandsBlank(template)) {
      return template
    }
    verticalRadical = if (template.settings.isStraightRadicalSymbol) {
      UebTranslations.VERTICAL_LINE_STRAIGHT
    } else {
      UebTranslations.VERTICAL_LINE_CURLY
    }
    //Override - not sure why the Curly option exists, as there is no way to switch between it in the menu.
    verticalRadical = UebTranslations.VERTICAL_LINE_STRAIGHT

    val dividend = TemplateNumberBuilder().wholeNum(template.brailleOperands[1]).build()
    templateLine.add(dividend)
    var quotient: TemplateNumber? = null
    if (template.settings.solutions > 0) {
      quotient = TemplateNumberBuilder().wholeNum(template.brailleSolutions[0]).build()
      templateLine.add(quotient)
    }
    TemplateNumber.makeColumnsEqualWidth((templateLine))
    val divisor = TemplateNumberBuilder().wholeNum(template.brailleOperands[0]).build()
    val dividendDivisorLine = Line()
    val minWhitespaceBetweenRadicalAndDividend = 1
    whitespaceBetweenRadicalAndDividend = (
        if (dividend.leftPadding > minWhitespaceBetweenRadicalAndDividend)
          " ".repeat(dividend.leftPadding)
        else
          " ".repeat(minWhitespaceBetweenRadicalAndDividend)
        ).length
    if (template.settings.passage == Passage.NUMERIC) {
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(divisor.toStringPart()))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(whitespaceBetweenDivisorAndRadical))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(verticalRadical!!))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(whitespaceBetweenRadicalAndDividend + dividend.leftPadding))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(dividend.toStringPart().trim { it <= ' ' }))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(dividend.rightPadding))
    } else {
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(UebTranslations.NUMBER_CHAR))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(divisor.toStringPart()))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(whitespaceBetweenDivisorAndRadical))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(verticalRadical!!))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(whitespaceBetweenRadicalAndDividend))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(UebTranslations.NUMBER_CHAR))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(dividend.leftPadding))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getTextSegment(dividend.toStringPart().trim { it <= ' ' }))
      dividendDivisorLine.elements
        .add(dividendDivisorLine.getWhitespaceSegment(dividend.rightPadding))
    }

    longestLine = dividendDivisorLine.toString().length
    if (quotient != null && template.settings.solutions > 0) {
      if (template.settings.passage == Passage.NUMERIC) {
        val line = Line()
        line.elements.add(
          line.getWhitespaceSegment(
            divisor.toStringPart().length
                + whitespaceBetweenRadicalAndDividend + verticalRadical!!.length
                + whitespaceBetweenDivisorAndRadical + quotient.leftPadding
          )
        )
        line.elements.add(line.getTextSegment(quotient.toStringPart().trim { it <= ' ' }))
        line.elements.add(line.getWhitespaceSegment(quotient.rightPadding))
        if (hasRemainder(template)) {
          // this is the remainder
          val remainder = TemplateNumberBuilder().wholeNum(template.brailleSolutions[1]).build()
          line.elements.add(line.getWhitespaceSegment(1))
          line.elements.add(
            line.getTextSegment(
              UebTranslations.REMAINDER_LOWERCASE + UebTranslations.NUMBER_CHAR + remainder.toStringPart()
            )
          )
        }
        template.lines.add(line)
      }
      else {
        val line = Line()
        line.elements.add(
          line.getWhitespaceSegment(
            (longestLine - UebTranslations.NUMBER_CHAR.length - quotient.toStringPart().length)
          )
        )
        line.elements.add(line.getTextSegment(UebTranslations.NUMBER_CHAR))
        line.elements.add(line.getTextSegment(quotient.toStringPart().trim(' ')))
        line.elements.add(line.getWhitespaceSegment(quotient.rightPadding))
        if (hasRemainder(template)) {
          // this is the remainder
          val remainder = TemplateNumberBuilder().wholeNum(template.brailleSolutions[1]).build()
          line.elements.add(line.getWhitespaceSegment(1))
          line.elements.add(
            line.getTextSegment(
              UebTranslations.REMAINDER_LOWERCASE + UebTranslations.NUMBER_CHAR + remainder.toStringPart()
            )
          )
          dividendDivisorLine.elements.add(
            dividendDivisorLine.getWhitespaceSegment(
              1 + UebTranslations.REMAINDER_LOWERCASE.length
                  + UebTranslations.NUMBER_CHAR.length + remainder.toStringPart().length
            )
          )
        }
        template.lines.add(line)
      }
      addLine(template, divisor, dividend)
    }
    template.lines.add(dividendDivisorLine)
    return template
  }

  private fun addLine(template: Template, divisor: TemplateNumber, dividend: TemplateNumber) {
    val line = Line()
    line.isSeparatorLine = true
    var remainder: TemplateNumber? = null
    if (hasRemainder(template)) {
      // this is the remainder
      remainder = TemplateNumberBuilder().wholeNum(template.brailleSolutions[1]).build()
    }
    if (template.settings.passage == Passage.NUMERIC) {
      val whitespace = divisor.toStringPart().length + whitespaceBetweenDivisorAndRadical
      line.elements.add(line.getWhitespaceSegment(whitespace))
      var lineChars = (longestLine - UebTranslations.HORIZONTAL_MODE.length - divisor.toStringPart().length
          - whitespaceBetweenDivisorAndRadical)
      if (remainder != null && hasRemainder(template)) {
        // this is the remainder
        lineChars += (1 + UebTranslations.REMAINDER_LOWERCASE.length
            + UebTranslations.NUMBER_CHAR.length + remainder.toStringPart().length)
      }
      line.elements.add(
        line.getTextSegment(
          UebTranslations.HORIZONTAL_MODE + UebTranslations.LINE_ASCII.repeat(lineChars)
        )
      )
    }
    else { //Non-Numeric passages
      val whitespace = (divisor.toStringPart().length + whitespaceBetweenDivisorAndRadical
          + UebTranslations.NUMBER_CHAR.length)
      line.elements.add(line.getWhitespaceSegment(whitespace))
      var lineChars = ((dividend.toStringPart().length + whitespaceBetweenRadicalAndDividend
          + verticalRadical!!.length) - UebTranslations.HORIZONTAL_MODE.length
          + UebTranslations.NUMBER_CHAR.length)
      if (remainder != null && hasRemainder(template)) {
        // this is the remainder
        lineChars += (1 + UebTranslations.REMAINDER_LOWERCASE.length
            + UebTranslations.NUMBER_CHAR.length + remainder.toStringPart().length)
      }
      line.elements.add(
        line.getTextSegment(
          UebTranslations.HORIZONTAL_MODE + UebTranslations.LINE_ASCII.repeat(lineChars)
        )
      )
    }
    template.lines.add(line)
  }
}
