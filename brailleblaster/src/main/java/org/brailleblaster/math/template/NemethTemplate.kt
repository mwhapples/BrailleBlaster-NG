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
import org.brailleblaster.math.spatial.SpatialMathEnum
import org.brailleblaster.math.template.Template.Companion.operandsBlank
import org.brailleblaster.math.template.TemplateNumber.TemplateNumberBuilder

class NemethTemplate {
    private val templateLine = ArrayList<TemplateNumber>()
    private var longestLine = 0
    fun format(template: Template): Template {
        if (operandsBlank(template)) {
            return template
        }
        val operator = template.brailleOperator
        val lastBrailleOperandIndex = template.brailleOperands.size - 1
        templateLine += template.brailleOperands.mapIndexed { index, s ->
            val wholeString = if (template.settings.operator == SpatialMathEnum.OPERATOR.MULTIPLY_ENUM && index == lastBrailleOperandIndex) {
                operator + s
            } else {
                s
            }
            TemplateNumberBuilder().wholeNum(wholeString).build()
        }
        templateLine += template.brailleSolutions.map { TemplateNumberBuilder().wholeNum(it).build() }
        TemplateNumber.makeColumnsEqualWidth(templateLine)
        templateLine.maxOf { it.toStringPart().length }.takeIf { it > longestLine }?.let { longestLine = it }
        for (i in templateLine.indices) {
            val number = templateLine[i]
            val line = Line()
            if (i == template.brailleOperands.size - 1) {
                line.elements.add(line.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
                line.elements.add(
                    line.getTextSegment(
                        if (template.settings.operator == SpatialMathEnum.OPERATOR.MULTIPLY_ENUM) "" else operator
                    )
                )
                line.elements.add(line.getWhitespaceSegment(number.leftPadding))
                line.elements.add(line.getTextSegment(number.toStringPart().trim { it <= ' ' }))
                line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                line.elements.add(line.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
            } else {
                line.elements.add(
                    line.getWhitespaceSegment(
                        (if (template.settings.operator == SpatialMathEnum.OPERATOR.MULTIPLY_ENUM) 0 else operator.length) + NemethTranslations.NEMETH_LINE_PADDING
                    )
                )
                line.elements.add(line.getWhitespaceSegment(number.leftPadding))
                line.elements.add(line.getTextSegment(number.toStringPart().trim { it <= ' ' }))
                line.elements.add(line.getWhitespaceSegment(number.rightPadding))
                line.elements.add(line.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
            }
            println("NemethTemplate: $line")
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
        line.elements
            .add(
                line.getTextSegment(
                    NemethTranslations.LINE_ASCII.repeat(
                        (longestLine +
                            (if (template.settings.operator == SpatialMathEnum.OPERATOR.MULTIPLY_ENUM) 0
                            else template.brailleOperator.length) + (NemethTranslations.NEMETH_LINE_PADDING * 2))
                    )
                )
            )
        template.lines.add(line)
    }
}
