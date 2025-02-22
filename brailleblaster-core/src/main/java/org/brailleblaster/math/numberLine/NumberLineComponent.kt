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

class NumberLineComponent @JvmOverloads constructor(
    var whole: String = "",
    var decimal: String = "",
    var numerator: String = "",
    var denominator: String = "",
    var isMinus: Boolean = false,
    var intervalType: IntervalType = IntervalType.WHOLE
) {

  val isEmpty: Boolean
    get() = whole.isEmpty() && decimal.isEmpty() && numerator.isEmpty() && denominator.isEmpty()


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
  companion object {
    @JvmStatic
    @JvmOverloads
    fun createNumberLineComponent(whole: String = "", decimal: String = "", numerator: String = "", denominator: String = ""): NumberLineComponent {
      var isMinus = false
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
      return NumberLineComponent(w, dec, n, d, isMinus)
    }
  }
}

@JvmOverloads
fun createNumberLineComponent(whole: String = "", decimal: String = "", numerator: String = "", denominator: String = ""): NumberLineComponent {
  return NumberLineComponent.createNumberLineComponent(whole, decimal, numerator, denominator)
}