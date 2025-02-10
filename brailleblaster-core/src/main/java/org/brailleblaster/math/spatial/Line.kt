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
package org.brailleblaster.math.spatial


class Line {
  var elements = ArrayList<Segment>()
  var isSeparatorLine = false
  var isPassageLine = false
  override fun toString(): String = elements.joinToString(separator = "") { it.toString() }

  fun length(): Int {
    return toString().length
  }

  interface Segment

  val lineBreakSegment: LineBreakSegment
    get() = LineBreakSegment()

  class LineBreakSegment : Segment {
    override fun toString(): String {
      return "\n\n"
    }
  }

  fun getWhitespaceSegment(space: Int): WhitespaceSegment {
    return WhitespaceSegment(space)
  }

  class WhitespaceSegment(space: Int) : Segment {
    @JvmField
    var space = 0

    init {
      this.space = space
    }

    override fun toString(): String {
      return " ".repeat(space)
    }
  }

  class TextSegment(text: String) : Segment {
    var text = ""

    init {
      this.text = text
    }

    override fun toString(): String {
      return text
    }
  }

  fun getTextSegment(brlString: String): TextSegment {
    return TextSegment(brlString)
  }

  companion object {
    fun print(l: Line) {
      for (i in l.elements.indices) {
        println(
          if (l.elements[i] is WhitespaceSegment) "Whitespace with value " + l.elements[i].toString().length
          else if (l.elements[i] is TextSegment) "Text with value " + l.elements[i].toString()
          else "New Line segment"
        )
      }
    }
  }
}