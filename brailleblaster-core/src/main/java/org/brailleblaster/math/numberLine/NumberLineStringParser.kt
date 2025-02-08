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

import org.brailleblaster.math.spatial.MathFormattingException
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

class NumberLineStringParser {
  fun unparseDecimal(component: NumberLineComponent): String {
    return (component.whole
        + if (component.decimal.isEmpty()) "" else "." + component.decimal)
  }

  fun unparseImproper(component: NumberLineComponent): String {
    return component.numerator + "/" + component.denominator
  }

  fun unparseMixed(component: NumberLineComponent): String {
    return ((if (component.whole.isEmpty()) "" else component.whole + " ")
        + if (component.numerator.isEmpty()) "" else component.numerator + "/" + component.denominator)
  }

  fun unparseWhole(component: NumberLineComponent): String {
    return component.whole
  }

  @Throws(MathFormattingException::class)
  fun parseComponents(): NumberLineText {
    return NumberLineText(
      interval=parseComponent(intervalString),
      lineEnd=parseComponent(lineEndString),
      lineStart=parseComponent(lineStartString),
      segment=
        NumberLineSegment(
          segmentEnd=parseComponent(segmentEnd),
          segmentStart=parseComponent(segmentStart))
      )
  }

  fun parseDecimal(s: String): NumberLineComponent {
    if (s.isEmpty()) {
      return NumberLineComponent()
    }
    var index = 0
    var c = s[index]
    val whole = StringBuilder()
    val decimal = StringBuilder()
    while (c != '.') {
      whole.append(c)
      if (index == s.length - 1) {
        break
      }
      index++
      c = s[index]
    }
    index++
    while (index < s.length) {
      c = s[index]
      decimal.append(c)
      index++
    }
    return NumberLineComponent(
      whole=whole.toString().trim { it <= ' ' },
      decimal=decimal.toString().trim { it <= ' ' })
  }

  fun parseMixed(s: String): NumberLineComponent {
    if (s.isEmpty()) {
      return NumberLineComponent()
    }
    var whole = ""
    var numerator = ""
    var denominator = ""
    val splitBlank = s.trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
      .toTypedArray()
    if (splitBlank.size > 1) {
      whole = splitBlank[0]
      val splitFrac = s.trim { it <= ' ' }.split("\\s+".toRegex(), limit = 2).toTypedArray()[1].split("/".toRegex())
        .dropLastWhile { it.isEmpty() }
        .toTypedArray()
      if (splitFrac.size > 1) {
        numerator = splitFrac[0]
        denominator = splitFrac[1]
      }
    } else {
      val splitFrac = s.trim { it <= ' ' }.split("/".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
      if (splitFrac.size > 1) {
        numerator = splitFrac[0]
        denominator = splitFrac[1]
      } else {
        whole = s
      }
    }
    return NumberLineComponent(
      whole=whole.trim { it <= ' ' },
      numerator=numerator.trim { it <= ' ' },
      denominator=denominator.trim { it <= ' ' })
  }

  fun parseImproper(s: String): NumberLineComponent {
    if (s.isEmpty()) {
      return NumberLineComponent()
    }
    var index = 0
    var c = s[index]
    val numerator = StringBuilder()
    val denominator = StringBuilder()
    while (c != '/') {
      numerator.append(c)
      if (index == s.length - 1) {
        break
      }
      index++
      c = s[index]
    }
    index++
    while (index < s.length) {
      c = s[index]
      denominator.append(c)
      index++
    }
    return NumberLineComponent(
      numerator=numerator.toString().trim { it <= ' ' },
      denominator=denominator.toString().trim { it <= ' ' })
  }

  fun parseWhole(s: String): NumberLineComponent {
    return if (s.isEmpty()) {
      NumberLineComponent()
    } else NumberLineComponent(whole=s.trim { it <= ' ' })
  }

  fun setLineStart(lineStart: String) {
    lineStartString = lineStart
  }

  fun setLineEnd(lineEnd: String) {
    lineEndString = lineEnd
  }

  var intervalString = ""
  var lineStartString = ""
    private set
  var lineEndString = ""
    private set
  var segmentStart = ""
  var segmentEnd = ""
  @Throws(MathFormattingException::class)
  fun interval(): IntervalType {
    return parseType(intervalString)
  }

  @Throws(MathFormattingException::class)
  fun lineStart(): IntervalType {
    return parseType(lineStartString)
  }

  @Throws(MathFormattingException::class)
  fun lineEnd(): IntervalType {
    return parseType(lineEndString)
  }

  @Throws(MathFormattingException::class)
  fun parseComponent(s: String): NumberLineComponent {
    if (s.isEmpty()) {
      return NumberLineComponent()
    }
    val mixed = Pattern.compile("-*-*\\s*\\d+\\s+\\d+\\s*/\\s*\\d+\\s*")
    val improper = Pattern.compile("-*-*\\s*\\d+\\s*/\\s*\\d+\\s*")
    val decimal = Pattern.compile("-*-*\\s*\\d*\\s*\\.\\s*\\d+\\s*")
    val whole = Pattern.compile("-*-*\\s*\\d+\\s*")
    val mixedMatcher = mixed.matcher(s)
    val improperMatcher = improper.matcher(s)
    val decimalMatcher = decimal.matcher(s)
    val wholeMatcher = whole.matcher(s)
    val type =
      if (mixedMatcher.matches()) IntervalType.MIXED else (if (improperMatcher.matches()) IntervalType.IMPROPER else if (decimalMatcher.matches()) IntervalType.DECIMAL else if (wholeMatcher.matches()) IntervalType.WHOLE else null)!!
    val component: NumberLineComponent = when (type) {
      IntervalType.DECIMAL -> parseDecimal(s)
      IntervalType.IMPROPER -> parseImproper(s)
      IntervalType.MIXED -> parseMixed(s)
      IntervalType.WHOLE -> parseWhole(s)
    }
    component.intervalType = type
    return component
  }

  @Throws(MathFormattingException::class)
  fun parseType(s: String): IntervalType {
    val mixed = Pattern.compile("-*-*\\s*\\d+\\s+\\d+\\s*/\\s*\\d+\\s*")
    val improper = Pattern.compile("-*-*\\s*\\d+\\s*/\\s*\\d+\\s*")
    val decimal = Pattern.compile("-*-*\\s*\\d*\\s*\\.\\s*\\d+\\s*")
    val whole = Pattern.compile("-*-*\\s*\\d+\\s*")
    val mixedMatcher = mixed.matcher(s)
    val improperMatcher = improper.matcher(s)
    val decimalMatcher = decimal.matcher(s)
    val wholeMatcher = whole.matcher(s)
    var type =
      if (mixedMatcher.matches()) IntervalType.MIXED else if (improperMatcher.matches()) IntervalType.IMPROPER else if (decimalMatcher.matches()) IntervalType.DECIMAL else if (wholeMatcher.matches()) IntervalType.WHOLE else null
    if (type == null && s.isNotEmpty()) {
      log.error("Pattern compile found no matches")
      throw MathFormattingException("Pattern compile found no matches")
    } else if (type == null) {
      type = IntervalType.WHOLE
    }
    return type
  }

  fun getNumberLineType(numberLine: NumberLine): IntervalType {
    /*
     * Hierarchy: Mixed fraction, improper fraction, decimal, whole
     */
    val text = numberLine.numberLineText
    if (text.interval.intervalType == IntervalType.MIXED || text.lineStart.intervalType == IntervalType.MIXED || text.lineEnd.intervalType == IntervalType.MIXED) {
      return IntervalType.MIXED
    }
    if (numberLine.shouldFormatSegment()) {
      if (text.segment.segmentStart.intervalType == IntervalType.MIXED || text.segment.segmentEnd.intervalType == IntervalType.MIXED) {
        return IntervalType.MIXED
      }
    }
    if (text.interval.intervalType == IntervalType.IMPROPER || text.lineStart.intervalType == IntervalType.IMPROPER || text.lineEnd.intervalType == IntervalType.IMPROPER) {
      return IntervalType.IMPROPER
    }
    if (numberLine.shouldFormatSegment()) {
      if (text.segment.segmentStart.intervalType == IntervalType.IMPROPER || text.segment.segmentEnd.intervalType == IntervalType.IMPROPER) {
        return IntervalType.IMPROPER
      }
    }
    if (text.interval.intervalType == IntervalType.DECIMAL || text.lineStart.intervalType == IntervalType.DECIMAL || text.lineEnd.intervalType == IntervalType.DECIMAL) {
      return IntervalType.DECIMAL
    }
    if (numberLine.shouldFormatSegment()) {
      if (text.segment.segmentStart.intervalType == IntervalType.DECIMAL || text.segment.segmentEnd.intervalType == IntervalType.DECIMAL) {
        return IntervalType.DECIMAL
      }
    }
    return IntervalType.WHOLE
  }

  @Throws(MathFormattingException::class)
  fun unparse(type: IntervalType?, text: NumberLineText) {
    intervalString = getTypeAndParse(text.interval)
    setLineStart(getTypeAndParse(text.lineStart))
    setLineEnd(getTypeAndParse(text.lineEnd))
    segmentStart = getTypeAndParse(text.segment.segmentStart)
    segmentEnd = getTypeAndParse(text.segment.segmentEnd)
  }

  private fun getTypeAndParse(component: NumberLineComponent): String {
    return when (component.intervalType) {
      IntervalType.DECIMAL -> unparseDecimal(component)
      IntervalType.IMPROPER -> unparseImproper(component)
      IntervalType.MIXED -> unparseMixed(component)
      IntervalType.WHOLE -> unparseWhole(component)
    }
  }

  @Throws(MathFormattingException::class)
  fun parse(): NumberLineText {
    return try {
      parseComponents()
    } catch (e: Exception) {
      throw MathFormattingException("Parsing error in number line string parser", e)
    }
  }

  fun clear() {
    intervalString = ""
    lineStartString = ""
    lineEndString = ""
    segmentStart = ""
    segmentEnd = ""
  }

  companion object {
    private val log = LoggerFactory.getLogger(NumberLineStringParser::class.java)
  }
}