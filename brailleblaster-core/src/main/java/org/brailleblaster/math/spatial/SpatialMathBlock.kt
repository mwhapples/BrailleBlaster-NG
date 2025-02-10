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

import nu.xom.Attribute
import nu.xom.Element
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.math.spatial.Line.*
import org.brailleblaster.settings.UTDManager.Companion.getCellsPerLine
import org.brailleblaster.utd.properties.EmphasisType
import org.brailleblaster.wordprocessor.WPManager

/**
 *
 *
 * For blocks that need to be translated as math and rendered in the text view
 * as braille
 */
object SpatialMathBlock {
    @Throws(MathFormattingException::class)
    fun format(container: Element, origLines: List<Line>) {
        val lines = clean(origLines)
        var lastBlock: Element? = null
        for ((i,line) in lines.withIndex()) {
            if (lineTooLong(line)) {
                throw MathFormattingException(
                    """
    Line ${i + 1}
    $line
    """.trimIndent()
                )
            }
            // make a block for the line
            val block = BBX.BLOCK.SPATIAL_MATH.create()
            var lineLength = 0
            for (segment in line.elements) {
                when (segment) {
                    is WhitespaceSegment -> {
                        // add tab
                        val tab = BBX.SPAN.TAB.create()
                        val spans = getSpans(segment.toString())
                        lineLength += spans
                        val attribute = BBX.SPAN.TAB.ATTRIB_VALUE.newAttribute(lineLength + 1)
                        tab.addAttribute(attribute)
                        block.appendChild(tab)
                    }

                    is TextSegment -> {
                        // add text
                        val emphasis = BBX.INLINE.EMPHASIS.create(EmphasisType.NO_TRANSLATE)
                        val string = segment.toString()
                        lineLength += string.length
                        emphasis.appendChild(Text(string))
                        block.appendChild(emphasis)
                    }

                    is LineBreakSegment -> {
                        lastBlock?.addAttribute(Attribute("linesAfter", "2"))
                    }
                }
            }
            if (block.childCount > 0) {
                container.appendChild(block)
                lastBlock = block
            }
        }
    }

    private fun lineTooLong(line: Line): Boolean {
        val cellsPerLine = getCellsPerLine(WPManager.getInstance().controller)
        return line.toString().length > cellsPerLine
    }

    private fun clean(lines: List<Line>): List<Line> {
        val newLines = ArrayList<Line>()
        for (l in lines) {
            val newLine = Line()
            var whitespace = 0
            var text = StringBuilder()
            for (s in l.elements) {
                when (s) {
                    is WhitespaceSegment -> {
                        if (text.isNotEmpty()) {
                            newLine.elements.add(newLine.getTextSegment(text.toString()))
                            text = StringBuilder()
                        }
                        whitespace += s.space
                    }

                    is LineBreakSegment -> {
                        newLine.elements.add(s)
                    }

                    else -> {
                        val temp = s.toString()
                        for (element in temp) {
                            if (Character.isWhitespace(element)) {
                                if (text.isNotEmpty()) {
                                    newLine.elements.add(newLine.getTextSegment(text.toString()))
                                    text = StringBuilder()
                                }
                                whitespace++
                            } else {
                                if (whitespace > 0) {
                                    newLine.elements.add(newLine.getWhitespaceSegment(whitespace))
                                    whitespace = 0
                                }
                                text.append(element)
                            }
                        }
                    }
                }
            }
            if (whitespace > 0) {
                newLine.elements.add(newLine.getWhitespaceSegment(whitespace))
            } else if (text.isNotEmpty()) {
                newLine.elements.add(newLine.getTextSegment(text.toString()))
            }
            newLines.add(newLine)
        }
        return newLines
    }

    private fun getSpans(string: String): Int {
        return string.takeWhile { it.isWhitespace() }.length
    }
}
