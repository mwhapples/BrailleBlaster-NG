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

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault

object NumberLineConstants {
  private val localeHandler = getDefault()
  var MARGIN_WARNING = localeHandler["nle.marginWarning"]
  val EMPTY_FIELD_WARNING = localeHandler["emptyFieldWarning"]
  @JvmField
	val USE_EDITOR_WARNING = localeHandler["readOnlyWarningNumberLine"]
  val DELETE_LABEL = localeHandler["deleteNumberLineButton"]
  val NOT_NUMBER_LINE = localeHandler["notNumberLine"]
  @JvmField
	val INTERVAL_LABEL = localeHandler["intervalLabel"]
  val INTERVAL_AS_FRACTION = localeHandler["intervalAsFraction"]
  val INTERVAL_AS_WHOLE_NUMBER = localeHandler["intervalAsWholeNumber"]
  val INTERVAL_AS_DECIMAL = localeHandler["intervalAsDecimal"]
  val INTERVAL_WARNING = localeHandler["intervalWarning"]
  @JvmField
	val REDUCE_FRACTION = localeHandler["reduceFraction"]
  val IMPROPER_FRACTION = localeHandler["improperFraction"]
  val MIXED_FRACTION = localeHandler["mixedFraction"]
  @JvmField
	val BEVELED_FRACTION = localeHandler["beveledFraction"]
  @JvmField
	val FULL_CIRCLE = localeHandler["fullCircle"]
  val EMPTY_CIRCLE = localeHandler["emptyCircle"]
  @JvmField
	var SEGMENT_START_LABEL = localeHandler["nle.segmentStart"]
  @JvmField
	var SEGMENT_END_LABEL = localeHandler["nle.segmentEnd"]
  @JvmField
	var LINE_START_LABEL = localeHandler["nle.lineStart"]
  @JvmField
	var LINE_END_LABEL = localeHandler["nle.lineEnd"]
  var PREVIEW_LABEL = localeHandler["nle.preview"]
  var OK_LABEL = localeHandler["lblOk"]
  var NUMERAL_WARNING = localeHandler["nle.numeralWarning"]
  @JvmField
	var STRETCH_LABEL = localeHandler["nle.stretch"]
  @JvmField
	var ARROW_LABEL = localeHandler["nle.arrow"]
  val INTERVAL_NUMERATOR_LABEL = localeHandler["intervalNumeratorLabel"]
  val INTERVAL_DENOMINATOR_LABEL = localeHandler["intervalDenominatorLabel"]
  val INTERVAL_DECIMAL_LABEL = localeHandler["intervalDecimalLabel"]
  val SEGMENT_START_NUMERATOR_LABEL = localeHandler["segmentStartNumeratorLabel"]
  val SEGMENT_START_DENOMINATOR_LABEL = localeHandler["segmentStartDenominatorLabel"]
  val SEGMENT_START_DECIMAL_LABEL = localeHandler["segmentStartDecimalLabel"]
  val SEGMENT_END_NUMERATOR_LABEL = localeHandler["segmentEndNumeratorLabel"]
  val SEGMENT_END_DENOMINATOR_LABEL = localeHandler["segmentEndDenominatorLabel"]
  val SEGMENT_END_DECIMAL_LABEL = localeHandler["segmentEndDecimalLabel"]
  val LINE_START_NUMERATOR_LABEL = localeHandler["lineStartNumeratorLabel"]
  val LINE_START_DENOMINATOR_LABEL = localeHandler["lineStartDenominatorLabel"]
  val LINE_START_DECIMAL_LABEL = localeHandler["lineStartDecimalLabel"]
  val LINE_END_NUMERATOR_LABEL = localeHandler["lineEndNumeratorLabel"]
  val LINE_END_DENOMINATOR_LABEL = localeHandler["lineEndDenominatorLabel"]
  val LINE_END_DECIMAL_LABEL = localeHandler["lineEndDecimalLabel"]
  val FILL_BOTH_SEGMENTS_WARNING = localeHandler["fillBothSegmentsWarning"]
  val INTERVAL_IS_ZERO_WARNING = localeHandler["intervalIsZeroWarning"]
  val DENOMINATOR_IS_ZERO_WARNING = localeHandler["denominatorIsZeroWarning"]
  @JvmField
	val REMOVE_LEADING_ZEROS_LABEL = localeHandler["removeLeadingZeros"]
  val CIRCLE_NONE_LABEL = localeHandler["noCircle"]
  const val ATTRIB_INTERVAL_NUMERATOR = "intervalNumerator"
  const val ATTRIB_INTERVAL_DENOMINATOR = "intervalDenominator"
  const val ATTRIB_INTERVAL_WHOLE = "interval"
  const val ATTRIB_REDUCE_FRACTION = "reduceFraction"
  @JvmField
	var ATTRIB_HAS_SEGMENT = "hasSegment"
  @JvmField
	var ATTRIB_SEGMENT_START_WHOLE = "segmentStart"
  @JvmField
	var ATTRIB_SEGMENT_END_WHOLE = "segmentEnd"
  @JvmField
	var ATTRIB_LINE_START_WHOLE = "lineStart"
  @JvmField
	var ATTRIB_LINE_END_WHOLE = "lineEnd"
  @JvmField
	var ATTRIB_SEGMENT_START_DECIMAL = "segmentStartDecimal"
  @JvmField
	var ATTRIB_SEGMENT_END_DECIMAL = "segmentEndDecimal"
  @JvmField
	var ATTRIB_LINE_START_DECIMAL = "lineStartDecimal"
  @JvmField
	var ATTRIB_LINE_END_DECIMAL = "lineEndDecimal"
  const val ATTRIB_INTERVAL_DECIMAL = "intervalDecimal"
  const val ATTRIB_INTERVAL_TYPE = "intervalType"
  val NUMERATOR_IS_ZERO_WARNING = localeHandler["numeratorZeroWarning"]
  val ABANDONED_NUMERATOR = localeHandler["abandonedNumeratorWarning"]
  val ABANDONED_DENOMINATOR = localeHandler["abandonedDenominatorWarning"]
  val OUT_OF_ORDER = localeHandler["outOfOrder"]
  @JvmField
	val MENU_SETTINGS = localeHandler["menuSettings"]
  val INTERVAL_TYPE_LABEL = localeHandler["intervalTypeLabel"]
  @JvmField
	val START_SEGMENT_SYMBOL_LABEL = localeHandler["startSegmentSymbolLabel"]
  @JvmField
	val END_SEGMENT_SYMBOL_LABEL = localeHandler["endSegmentSymbolLabel"]
  val NUMERATOR_LABEL = localeHandler["numerator"]
  val DENOMINATOR_LABEL = localeHandler["denominator"]
  val DECIMAL_LABEL = localeHandler["decimal"]
  val WHOLE_LABEL = localeHandler["whole"]
  @JvmField
	val NUMBER_LINE_TYPE = localeHandler["numberLineType"]
  @JvmField
	val NUMBER_POINTS = localeHandler["numberOfPoints"]
  val SEGMENT_LABEL = localeHandler["segment"]
  @JvmField
	val SECTION_TYPE = localeHandler["segmentOrPoints"]
  @JvmField
	val POINTS_CIRCLE_LABEL = localeHandler["pointsCircleLabel"]
  @JvmField
	val POINT = localeHandler["point"]
  @JvmField
	val TRANSLATION_TYPE = localeHandler["translationType"]
  @JvmField
	val LABEL_POSITION = localeHandler["labelPosition"]
  val PASSAGE_TYPE = localeHandler["passageType"]
  @JvmField
	val NUMBER_LINE_ALLOWED_CHARS = localeHandler["numberLineAllowedChars"]
  @JvmField
	val MARKER_LABEL = localeHandler["numberLineMarker"]
  val INTERVAL_IS_NEGATIVE_WARNING = localeHandler["intervalNegativeWarning"]
  @JvmField
	val DONE_LABEL = localeHandler["doneLabel"]
  @JvmField
	val TRANSLATION_LABEL = localeHandler["translationLabel"]
  @JvmField
	var ATTRIB_SEGMENT_FILL_START = "segmentFillStart"
  var ATTRIB_SEGMENT_EMPTY_START = "segmentEmptyStart"
  var ATTRIB_LINE_FILL_START = "lineFillStart"
  var ATTRIB_LINE_EMPTY_START = "lineEmptyStart"
  @JvmField
	var ATTRIB_ARROW = "arrow"
  @JvmField
	var ATTRIB_STRETCH = "stretch"
  var ATTRIB_FORMATTED_SEGMENT = "formattedSegment"
  var ATTRIB_FORMATTED_LINE = "formattedLine"
  var ATTRIB_FORMATTED_POINTS = "formattedPoints"
  @JvmField
	var ATTRIB_SEGMENT_START_NUMERATOR = "segmentStartNumerator"
  @JvmField
	var ATTRIB_SEGMENT_START_DENOMINATOR = "segmentStartDenominator"
  @JvmField
	var ATTRIB_SEGMENT_END_NUMERATOR = "segmentEndNumerator"
  @JvmField
	var ATTRIB_SEGMENT_END_DENOMINATOR = "segmentEndDenominator"
  @JvmField
	var ATTRIB_LINE_START_NUMERATOR = "lineStartNumerator"
  @JvmField
	var ATTRIB_LINE_START_DENOMINATOR = "lineStartDenominator"
  @JvmField
	var ATTRIB_LINE_END_NUMERATOR = "lineEndNumerator"
  @JvmField
	var ATTRIB_LINE_END_DENOMINATOR = "lineEndDenominator"
  @JvmField
	var ATTRIB_LEADING_ZEROS = "leadingZeros"
}