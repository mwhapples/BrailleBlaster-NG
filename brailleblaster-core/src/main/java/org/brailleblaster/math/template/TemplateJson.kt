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

import kotlinx.serialization.Serializable
import org.brailleblaster.math.spatial.ISpatialMathContainerJson
import org.brailleblaster.math.spatial.MathText
import org.brailleblaster.math.spatial.SpatialMathEnum
import org.brailleblaster.math.spatial.SpatialMathEnum.OPERATOR
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage

@Serializable
class TemplateJson @JvmOverloads constructor(
    private var straightRadical: Boolean = false,
    var operator: OPERATOR = TemplateSettings.DEFAULT_OPERATOR,
    private var templateType: SpatialMathEnum.TemplateType = TemplateSettings.DEFAULT_TYPE,
    var operands: List<String> = emptyList(),
    private var solutions: List<String> = emptyList(),
    var passage: Passage = Passage.NONE,
    var identifier: MathText = MathText(),
    private var identifierAsMath: Boolean = false,
    var linear: Boolean = false
) : ISpatialMathContainerJson {

    override fun jsonToContainer(): Template {
        val template = Template()
        template.settings.isStraightRadicalSymbol = straightRadical
        template.settings.operator = operator
        template.settings.type = templateType
        template.printOperands = operands
        template.printSolutions = solutions
        template.settings.operands = operands.size
        template.settings.solutions = solutions.size
        template.settings.passage = passage
        template.identifier = identifier
        template.settings.isTranslateIdentifierAsMath = identifierAsMath
        template.settings.isLinear = linear
        return template
    }
}

fun Template.createTemplateJson(): TemplateJson = TemplateJson(straightRadical = settings.isStraightRadicalSymbol, operator = settings.operator, templateType = settings.type, operands = printOperands, solutions = printSolutions, passage = settings.passage, identifier = identifier, identifierAsMath = settings.isTranslateIdentifierAsMath, linear = settings.isLinear)