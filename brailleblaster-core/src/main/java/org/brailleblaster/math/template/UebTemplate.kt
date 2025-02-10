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
import org.brailleblaster.math.template.Template.Companion.operandsBlank
import org.brailleblaster.math.template.TemplateNumber.TemplateNumberBuilder

class UebTemplate {
    private val templateLine = ArrayList<TemplateNumber>()
    private var longestLine = 0
    fun format(template: Template): Template {
        if (operandsBlank(template)) {
            return template
        }
        for (i in template.brailleOperands.indices) {
            val wholeString = template.brailleOperands[i]
            templateLine.add(TemplateNumberBuilder().wholeNum(wholeString).build())
        }
        for (i in template.brailleSolutions.indices) {
            val wholeString = template.brailleSolutions[i]
            templateLine.add(TemplateNumberBuilder().wholeNum(wholeString).build())
        }
        TemplateNumber.makeColumnsEqualWidth(templateLine)
        val operator = template.brailleOperator
        val operatorLine = templateLine[template.brailleOperands.size - 1].toStringWhole(template)
        if (operator.length + operatorLine.length > longestLine) {
            longestLine = operator.length + operatorLine.length
        }
        for (number in templateLine) {
            if (number.toStringPart().length > longestLine) {
                longestLine = number.toStringPart().length
            }
        }
        for (i in templateLine.indices) {
            val number = templateLine[i]
            val line = Line()
            if (template.settings.passage == Passage.NUMERIC) {
                if (i == template.brailleOperands.size - 1) {
                    line.elements
                        .add(
                            line.getTextSegment(
                                operator +
                                    if (number.isMinus) UebTranslations.MINUS
                                    else if (number.isColumnHasMinus) " ".repeat(UebTranslations.MINUS.length)
                                    else ""
                            )
                        )
                    line.elements.add(line.getWhitespaceSegment(number.leftPadding))
                    line.elements.add(line.getTextSegment(number.toStringPart().trim { it <= ' ' }))
                    line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                } else {
                    line.elements.add(line.getWhitespaceSegment(operator.length + number.leftPadding))
                    line.elements
                        .add(
                            line.getTextSegment((
                                if (number.isMinus) UebTranslations.MINUS
                                else if (number.isColumnHasMinus) " ".repeat(UebTranslations.MINUS.length)
                                else ""
                            ) + number.toStringPart().trim { it <= ' ' })
                        )
                    line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                }
            } else {
                if (i == template.brailleOperands.size - 1) {
                    line.elements
                        .add(
                            line.getTextSegment(
                                operator + (
                                    if (number.isMinus) UebTranslations.MINUS
                                    else if (number.isColumnHasMinus) " ".repeat(UebTranslations.MINUS.length)
                                    else ""
                                ) + UebTranslations.NUMBER_CHAR
                            )
                        )
                    line.elements.add(line.getWhitespaceSegment(number.leftPadding))
                    line.elements.add(line.getTextSegment(number.toStringPart().trim { it <= ' ' }))
                    line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                } else {
                    line.elements.add(line.getWhitespaceSegment(operator.length))
                    line.elements.add(line.getWhitespaceSegment(number.leftPadding))
                    line.elements
                        .add(
                            line.getTextSegment((
                                if (number.isMinus) UebTranslations.MINUS
                                else if (number.isColumnHasMinus) " ".repeat(UebTranslations.MINUS.length)
                                else ""
                                ) + UebTranslations.NUMBER_CHAR + number.toStringPart().trim { it <= ' ' })
                        )
                    line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                }
            }
            template.lines.add(line)
            if (i == template.brailleOperands.size - 1) {
                addLine(template)
            }
        }
        return template
    }

    private fun addLine(template: Template) {
        val line = Line()
        line.isSeparatorLine = true
        line.elements.add(line.getWhitespaceSegment(template.brailleOperator.length))
        var lineChars = (longestLine - UebTranslations.HORIZONTAL_MODE.length
                - template.brailleOperator.length)
        if (lineChars < UebTranslations.MIN_LINE_CHARS) {
            lineChars = UebTranslations.MIN_LINE_CHARS
        }
        line.elements.add(
            line.getTextSegment(
                UebTranslations.HORIZONTAL_MODE + UebTranslations.LINE_ASCII.repeat(lineChars)
            )
        )
        template.lines.add(line)
    }
}
