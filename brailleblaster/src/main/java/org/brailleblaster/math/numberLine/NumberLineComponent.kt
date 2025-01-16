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

import org.apache.commons.lang3.math.Fraction
import org.brailleblaster.math.spatial.MathFormattingException
import org.brailleblaster.math.spatial.SpatialMathEnum.IntervalType
import kotlin.math.abs

class NumberLineComponent(whole: String = "", decimal: String = "", numerator: String = "", denominator: String = "") {
  var whole = ""
  var decimal = ""
  var numerator = ""
  var denominator = ""
  var isMinus = false
  var intervalType = IntervalType.WHOLE

  init {
    var w = whole
    if (w.isNotEmpty()) {
      val integer = w.toInt()
      if (integer < 0) {
        isMinus = true
        w = abs(integer).toString()
      }
    }
    var dec = decimal
    if (dec.isNotEmpty()) {
      val integer = dec.toInt()
      if (integer < 0) {
        isMinus = true
        dec = abs(integer).toString()
      }
    }
    var n = numerator
    if (n.isNotEmpty()) {
      val integer = n.toInt()
      if (integer < 0) {
        isMinus = true
        n = abs(integer).toString()
      }
    }
    var d = denominator
    if (d.isNotEmpty()) {
      val integer = d.toInt()
      if (integer < 0) {
        isMinus = true
        d = abs(integer).toString()
      }
    }
    if (isMinus && w.isEmpty() && n.isNotEmpty()) {
      n = "-$n"
    } else if (isMinus) {
      w = "-$w"
    }
    this.whole = w
    this.decimal = dec
    this.numerator = n
    this.denominator = d
  }

  val isEmpty: Boolean
    get() = whole.isEmpty() && decimal.isEmpty() && numerator.isEmpty() && denominator.isEmpty()

  class NumberLineComponentBuilder {
    var whole = ""
    var decimal = ""
    var numerator = ""
    var denominator = ""
    fun whole(s: String): NumberLineComponentBuilder {
      whole = s
      return this
    }

    fun decimal(s: String): NumberLineComponentBuilder {
      decimal = s
      return this
    }

    fun numerator(s: String): NumberLineComponentBuilder {
      numerator = s
      return this
    }

    fun denominator(s: String): NumberLineComponentBuilder {
      denominator = s
      return this
    }

    fun build(): NumberLineComponent {
      return NumberLineComponent(whole, decimal, numerator, denominator)
    }
  }

  @get:Throws(MathFormattingException::class)
  val fraction: Fraction
    get() = try {
      if (numerator.isNotBlank()) {
        if (whole.isNotBlank()) {
          Fraction.getFraction(whole.toInt(), numerator.toInt(), denominator.toInt())
        } else {
          Fraction.getFraction(numerator.toInt(), denominator.toInt())
        }
      } else {
        if (decimal.isNotBlank()) {
          Fraction.getFraction("$whole.$decimal")
        } else {
          Fraction.getFraction(whole)
        }
      }
    } catch (e: Exception) {
      throw MathFormattingException(MathFormattingException.FRACTION_PARSING, e)
    }

  @Throws(MathFormattingException::class)
  operator fun compareTo(o: NumberLineComponent): Int {
    val thisFraction = fraction
    val thatFraction = o.fraction
    return thisFraction.compareTo(thatFraction)
  }
}