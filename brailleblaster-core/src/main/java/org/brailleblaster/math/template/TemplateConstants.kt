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

import org.brailleblaster.utils.localization.LocaleHandler.Companion.getDefault

object TemplateConstants {
  private val localeHandler = getDefault()
  val ROW_GROUP = localeHandler["rowGroup"]
  val EMPTY_TEMPLATE_WARNING = localeHandler["emptyTemplateWarning"]
  val EMPTY_TEMPLATE_SHELL_LABEL = localeHandler["emptyTemplateShellLabel"]
  val INSERT_DELETE_GROUP = localeHandler["insertDeleteGroup"]
  val OK_LABEL = localeHandler["insertTemplate"]
  val ADD_ROW = localeHandler["addRow"]
  val ADD_COL = localeHandler["addCol"]
  val REMOVE_ROW = localeHandler["removeRow"]
  val REMOVE_COL = localeHandler["removeCol"]
	val USE_EDITOR_WARNING = localeHandler["useEditorWarning"]
  val TEMPLATE_LABEL = localeHandler["templateLabel"]
  val DELETE_TEMPLATE = localeHandler["deleteTemplate"]
  val TOO_MANY_OPERANDS_WARNING = localeHandler["tooManyOperands"]
  @JvmField
	val TEMPLATE_TYPE_LABEL = localeHandler["templateTypeLabel"]
  val TYPE_SETTINGS = localeHandler["typeSettings"]
  val ADD_REMOVE_GROUP = localeHandler["addRemoveGroup"]
  @JvmField
	val IDENTIFIER_GROUP = localeHandler["identifierGroup"]
  @JvmField
	val IDENTIFIER_LABEL = localeHandler["identifier"]
	val STRAIGHT_VERTICAL = localeHandler["straightVertical"]
  const val OPERATOR_LABEL_KEY = "operatorLabel"
  @JvmField
	val OPERATOR_LABEL = localeHandler[OPERATOR_LABEL_KEY]
  const val NUMERIC_PASSAGE_LABEL_KEY = "numericPassage"
  val NUMERIC_PASSAGE_LABEL = localeHandler[NUMERIC_PASSAGE_LABEL_KEY]
  val ADD_OPERAND = localeHandler["addOperand"]
  val REMOVE_OPERAND = localeHandler["removeOperand"]
  val ADD_SOLUTION = localeHandler["addSolution"]
  val REMOVE_SOLUTION = localeHandler["removeSolution"]
  @JvmField
	val OPERAND_GROUP = localeHandler["operandGroup"]
  @JvmField
	val SOLUTION_GROUP = localeHandler["solutionGroup"]
  const val SIMPLE_KEY = "simpleTemplate"
  const val FRACTION_KEY = "fractionTemplate"
  const val OPERAND_LABEL_KEY = "operandLabel"
  const val SOLUTION_LABEL_KEY = "solutionLabel"

	val SIMPLE_COMPONENT_OPERAND_NAMES = arrayOf(OPERAND_LABEL_KEY)
  const val OPERAND_DENOMINATOR_LABEL_KEY = "operandDenominator"
  const val OPERAND_NUMERATOR_LABEL_KEY = "operandNumerator"
  private const val DENOMINATOR_LABEL_KEY = "denominator"
  private const val WHOLE_LABEL_KEY = "whole"
  private const val NUMERATOR_LABEL_KEY = "numerator"

	val FRACTION_COMPONENT_OPERAND_NAMES = arrayOf(
    WHOLE_LABEL_KEY, NUMERATOR_LABEL_KEY,
    DENOMINATOR_LABEL_KEY
  )

	val SIMPLE_COMPONENT_SOLUTION_NAMES = arrayOf(SOLUTION_LABEL_KEY)
  const val SOLUTION_DENOMINATOR_LABEL_KEY = "solutionDenominator"
  const val SOLUTION_NUMERATOR_LABEL_KEY = "solutionNumerator"

	val FRACTION_COMPONENT_SOLUTION_NAMES = arrayOf(
    WHOLE_LABEL_KEY, NUMERATOR_LABEL_KEY,
    DENOMINATOR_LABEL_KEY
  )
	val DIVISION_KEY = localeHandler["divisionTemplate"]
  val DIVISOR = localeHandler["divisor"]
  val DIVIDEND = localeHandler["dividend"]
	val RADICAL_SOLUTION_NAMES = arrayOf(SOLUTION_LABEL_KEY)
	val RADICAL_OPERAND_NAMES = arrayOf(DIVISOR, DIVIDEND)
  const val PLUS_KEY = "plus"
  const val MINUS_KEY = "minus"
  const val MULTIPLY_KEY = "multiply"
  const val DIVIDE_KEY = "divide"
  const val PLUS_SYMBOL = "+"
  const val MINUS_SYMBOL = "-"
  const val MULTIPLY_CROSS_SYMBOL = "\u00D7"
  const val MULTIPLY_DOT_SYMBOL = "\u00B7"

	val LINEAR = localeHandler["linear"]
  const val USER_SETTINGS_TEMPLATE_TYPE = "template.type"
  const val USER_SETTINGS_TEMPLATE_OPERATOR = "template.operator"
  const val USER_SETTINGS_TEMPLATE_PASSAGE = "template.passage"
  @JvmField
	val SETTINGS = localeHandler["settings"]
  val CANCEL_LABEL = localeHandler["cancel"]
  val COL_GROUP = localeHandler["colGroup"]
  @JvmField
	val IDENTIFIER_TRANSLATION = localeHandler["identifierTranslation"]
  @JvmField
	val MATH_TRANSLATION = localeHandler["mathTranslation"]
  val LITERARY_TRANSLATION = localeHandler["literaryTranslation"]
  const val USER_SETTINGS_OPERANDS = "t.operands"
  const val USER_SETTINGS_SOLUTION = "t.solution"
  const val USER_SETTINGS_VERTICAL = "t.vertical"
  const val USER_SETTINGS_IDENTIFIER_TRANSLATION = "t.identifierTranslation"
  const val USER_SETTINGS_LINEAR = "t.linear"
  @JvmField
	val REMAINDER = localeHandler["remainder"]
  @JvmField
	val FALSE = localeHandler["false"]
  @JvmField
	val TRUE = localeHandler["true"]
}