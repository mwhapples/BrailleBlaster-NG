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
import org.brailleblaster.math.spatial.NemethTranslations
import org.brailleblaster.math.spatial.UebTranslations
import org.brailleblaster.math.template.Template.Companion.hasRemainder
import org.brailleblaster.math.template.Template.Companion.operandsBlank
import org.brailleblaster.math.template.TemplateNumber.TemplateNumberBuilder
import org.brailleblaster.math.template.TemplateSettings.Companion.shouldFormatLinear

class NemethRadical {
    private val templateLine = ArrayList<TemplateNumber>()
    var verticalRadical: String? = null

    fun format(template: Template): Template {
        if (operandsBlank(template)) {
            return template
        }
        verticalRadical = if (template.settings.isStraightRadicalSymbol) {
            NemethTranslations.VERTICAL_LINE_STRAIGHT
        } else {
            NemethTranslations.VERTICAL_LINE_CURLY
        }
        //Override - not sure why the Curly option exists, as there is no way to switch between it in the menu.
        verticalRadical = NemethTranslations.VERTICAL_LINE_STRAIGHT

        if (shouldFormatLinear(template)) {
            formatLinear(template)
        } else {
            val dividend = TemplateNumberBuilder()
                .wholeNum(template.brailleOperands[1])
                .build()
            templateLine.add(dividend)
            var quotient: TemplateNumber? = null
            if (template.settings.solutions > 0) {
                quotient = TemplateNumberBuilder().wholeNum(
                    template.brailleSolutions[0]
                )
                    .build()
                templateLine.add(quotient)
            }
            var remainder: TemplateNumber? = null
            if (hasRemainder(template)) {
                // this is the remainder
                remainder = TemplateNumberBuilder()
                    .wholeNum(template.brailleSolutions[1]).build()
            }
            TemplateNumber.makeColumnsEqualWidth((templateLine))
            val divisor = TemplateNumberBuilder().wholeNum(template.brailleOperands[0]).build()
            val dividendDivisorLine = Line()
            dividendDivisorLine.elements.add(dividendDivisorLine.getTextSegment(divisor.toStringPart()))
            dividendDivisorLine.elements.add(dividendDivisorLine.getTextSegment(verticalRadical!!))
            dividendDivisorLine.elements.add(dividendDivisorLine.getWhitespaceSegment(dividend.leftPadding))
            dividendDivisorLine.elements.add(
                dividendDivisorLine.getTextSegment(
                    dividend.toStringPart().trim { it <= ' ' })
            )
            dividendDivisorLine.elements.add(dividendDivisorLine.getWhitespaceSegment(dividend.rightPadding))
            dividendDivisorLine.elements
                .add(dividendDivisorLine.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
            val quotientLine = Line()
            if (quotient != null && template.settings.solutions > 0) {
                quotientLine.elements.add(
                    quotientLine
                        .getWhitespaceSegment(divisor.toStringPart().length + verticalRadical!!.length + quotient.leftPadding)
                )
                quotientLine.elements.add(quotientLine.getTextSegment(quotient.toStringPart().trim { it <= ' ' }))
                quotientLine.elements.add(quotientLine.getWhitespaceSegment(quotient.rightPadding))
                quotientLine.elements
                    .add(quotientLine.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
                if (remainder != null && hasRemainder(template)) {
                    // this is the remainder
                    quotientLine.elements
                        .add(quotientLine.getTextSegment(NemethTranslations.REMAINDER_CAPITAL + remainder.toStringPart()))
                    quotientLine.elements
                        .add(quotientLine.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
                    dividendDivisorLine.elements
                        .add(
                            dividendDivisorLine.getWhitespaceSegment(
                                NemethTranslations.REMAINDER_CAPITAL.length
                                        + remainder.toStringPart().length
                                        + NemethTranslations.NEMETH_LINE_PADDING
                            )
                        )
                }
                template.lines.add(quotientLine)
            }

            addLine(template, divisor, dividend)
            template.lines.add(dividendDivisorLine)
        }
        return template
    }

    private fun formatLinear(template: Template) {
        val dividend = template.brailleOperands[1].replace(UebTranslations.NUMBER_CHAR.toRegex(), "")
        val divisor = template.brailleOperands[0].replace(UebTranslations.NUMBER_CHAR.toRegex(), "")
        val equation = NemethTranslations.NUM_CHAR + divisor + verticalRadical + dividend
        val l = Line()
        l.elements.add(l.getTextSegment(equation))
        template.lines.add(l)
    }

    private fun addLine(template: Template, divisor: TemplateNumber, dividend: TemplateNumber) {
        val line = Line()
        line.isSeparatorLine = true
        val whitespace = divisor.toStringPart().length
        line.elements.add(line.getWhitespaceSegment(whitespace))
        var lineChars = (dividend.toStringPart().length + verticalRadical!!.length
                + NemethTranslations.NEMETH_LINE_PADDING)
        if (hasRemainder(template)) {
            // this is the remainder
            val remainder = TemplateNumberBuilder()
                .wholeNum(template.brailleSolutions[1]).build()
            lineChars += (NemethTranslations.REMAINDER_CAPITAL.length
                    + remainder.toStringPart().length
                    + NemethTranslations.NEMETH_LINE_PADDING)
        }
        line.elements.add(line.getTextSegment(NemethTranslations.LINE_ASCII.repeat(lineChars)))
        template.lines.add(line)
    }
}
