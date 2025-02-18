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

import org.brailleblaster.math.spatial.ISpatialMathContainer
import org.brailleblaster.math.spatial.ISpatialMathContainerJson
import org.brailleblaster.math.spatial.MathText
import org.brailleblaster.math.spatial.SpatialMathEnum
import org.brailleblaster.math.spatial.SpatialMathEnum.OPERATOR
import org.brailleblaster.math.spatial.SpatialMathEnum.Passage

class TemplateJson() : ISpatialMathContainerJson {
    private var straightRadical = false
    lateinit var operator: OPERATOR
    private lateinit var templateType: SpatialMathEnum.TemplateType
    var operands: List<String> = emptyList()
    private var solutions: List<String> = emptyList()
    lateinit var passage: Passage
    var identifier: MathText? = null
    private var identifierAsMath = false
    var linear = false
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
        template.identifier = identifier!!
        template.settings.isTranslateIdentifierAsMath = identifierAsMath
        template.settings.isLinear = linear
        return template
    }

    fun containerToJson(container: ISpatialMathContainer): TemplateJson {
        val template = container as Template
        straightRadical = template.settings.isStraightRadicalSymbol
        operator = template.settings.operator
        templateType = template.settings.type
        operands = template.printOperands
        solutions = template.printSolutions
        passage = template.settings.passage
        identifier = template.identifier
        identifierAsMath = template.settings.isTranslateIdentifierAsMath
        linear = template.settings.isLinear
        return this
    }
}

fun Template.createTemplateJson(): TemplateJson = TemplateJson().containerToJson(this)