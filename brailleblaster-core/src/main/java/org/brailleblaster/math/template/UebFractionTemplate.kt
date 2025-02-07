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

class UebFractionTemplate {
    private val templateLine = ArrayList<TemplateFraction>()
    private var longestLine = 0
    fun format(template: Template): Template {
        if (operandsBlank(template)) {
            return template
        }
        val components = 3
        run {
            var i = 0
            while (i < template.brailleOperands.size) {
                val wholeString = template.brailleOperands[i]
                val numString = template.brailleOperands[i + 1]
                val denString = template.brailleOperands[i + 2]
                val wholeTn = TemplateNumberBuilder().wholeNum(wholeString).build()
                val numTn = TemplateNumberBuilder().wholeNum(numString).build()
                val denTn = TemplateNumberBuilder().wholeNum(denString).build()
                templateLine.add(
                    TemplateFraction(whole=wholeTn, den=denTn, num=numTn, passage=template.settings.passage)
                )
                i += components
            }
        }
        run {
            var i = 0
            while (i < template.brailleSolutions.size) {
                val wholeString = template.brailleSolutions[i]
                val numString = template.brailleSolutions[i + 1]
                val denString = template.brailleSolutions[i + 2]
                val wholeTn = TemplateNumberBuilder().wholeNum(wholeString).build()
                val numTn = TemplateNumberBuilder().wholeNum(numString).build()
                val denTn = TemplateNumberBuilder().wholeNum(denString).build()
                templateLine.add(
                    TemplateFraction(whole=wholeTn, den=denTn, num=numTn, passage=template.settings.passage)
                )
                i += components
            }
        }
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getWhole(templateLine))
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getNumerators(templateLine))
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getDenominators(templateLine))
        TemplateFraction.makeColumnsEqualWidth(templateLine)
        val operator = template.brailleOperator
        for (fraction in templateLine) {
            if (fraction.toString().length > longestLine) {
                longestLine = fraction.toString().length
            }
        }
        val operatorLine = templateLine[template.brailleOperands.size / components - 1].toString()
        if (operator.length + operatorLine.length > longestLine) {
            longestLine = operatorLine.length
        }
        for (i in templateLine.indices) {
            val fraction = templateLine[i]
            val line = Line()
            if (i == template.brailleOperands.size / components - 1) {
                line.elements.add(line.getTextSegment(operator))
                fraction.addToLine(line)
            } else {
                line.elements.add(line.getWhitespaceSegment(operator.length))
                fraction.addToLine(line)
            }
            template.lines.add(line)
            if (i == template.brailleOperands.size / components - 1) {
                addLine(template)
            }
        }
        return template
    }

    private fun addLine(template: Template) {
        val line = Line()
        line.isSeparatorLine = true
        if (template.settings.passage == Passage.NUMERIC) {
            line.elements.add(line.getWhitespaceSegment(template.brailleOperator.length))
            var lineChars = longestLine
            if (lineChars < UebTranslations.MIN_LINE_CHARS) {
                lineChars = UebTranslations.MIN_LINE_CHARS
            }
            line.elements.add(
                line.getTextSegment(
                    UebTranslations.HORIZONTAL_MODE + UebTranslations.LINE_ASCII.repeat(lineChars)
                )
            )
        } else {
            line.elements.add(line.getWhitespaceSegment(template.brailleOperator.length))
            var lineChars = longestLine - UebTranslations.HORIZONTAL_MODE.length
            if (lineChars < UebTranslations.MIN_LINE_CHARS) {
                lineChars = UebTranslations.MIN_LINE_CHARS
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
