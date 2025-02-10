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
import org.brailleblaster.math.template.Template.Companion.operandsBlank
import org.brailleblaster.math.template.TemplateNumber.TemplateNumberBuilder

class NemethFractionTemplate {
    private val templateLine = ArrayList<TemplateFraction>()
    private var longestLine = 0
    fun format(template: Template): Template {
        if (operandsBlank(template)) {
            return template
        }
        val components = 3
        templateLine += template.brailleOperands.chunked(components) {
            val wholeTn = TemplateNumberBuilder().wholeNum(it[0]).build()
            val numTn = TemplateNumberBuilder().wholeNum(it[1]).build()
            val denTn = TemplateNumberBuilder().wholeNum(it[2]).build()
            TemplateFraction(whole=wholeTn, den=denTn, num=numTn)
        }
        templateLine += template.brailleSolutions.chunked(components) {
            val wholeTn = TemplateNumberBuilder().wholeNum(it[0]).build()
            val numTn = TemplateNumberBuilder().wholeNum(it[1]).build()
            val denTn = TemplateNumberBuilder().wholeNum(it[2]).build()
            TemplateFraction(whole=wholeTn,den=denTn,num=numTn)
        }
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getWhole(templateLine))
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getNumerators(templateLine))
        TemplateNumber.makeColumnsEqualWidth(TemplateFraction.getDenominators(templateLine))
        TemplateFraction.makeColumnsEqualWidth(templateLine)
        val operator = template.brailleOperator
        templateLine.maxOf { it.toString().length }.takeIf { it > longestLine }?.let { longestLine = it }
        for (i in templateLine.indices) {
            val fraction = templateLine[i]
            val line = Line()
            if (i == template.brailleOperands.size / components - 1) {
                line.elements.add(
                    line.getWhitespaceSegment(
                        longestLine - fraction.toString().length + NemethTranslations.NEMETH_LINE_PADDING
                    )
                )
                line.elements.add(line.getTextSegment(operator))
            } else {
                line.elements.add(line.getWhitespaceSegment(longestLine - fraction.toString().length + operator.length + NemethTranslations.NEMETH_LINE_PADDING))
            }
            fraction.addToLine(line)
            line.elements.add(line.getWhitespaceSegment(NemethTranslations.NEMETH_LINE_PADDING))
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
        line.elements.add(
            line.getTextSegment(
                NemethTranslations.LINE_ASCII.repeat(
                    longestLine + template.brailleOperator.length + NemethTranslations.NEMETH_LINE_PADDING * 2
                )
            )
        )
        template.lines.add(line)
    }
}
