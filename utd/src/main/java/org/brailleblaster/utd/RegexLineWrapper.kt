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
package org.brailleblaster.utd

import org.brailleblaster.utd.LineWrapper.InsertionResult
import org.brailleblaster.utd.LineWrapper.LineBreakResult
import org.brailleblaster.utd.exceptions.NoLineBreakPointException
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

class RegexLineWrapper @JvmOverloads constructor(patterns: List<InsertionPatternEntry>? = null) : LineWrapper {
    private val log = LoggerFactory.getLogger(RegexLineWrapper::class.java)
    private val matchPatterns: MutableMap<Pattern, String?> = LinkedHashMap()
    @Throws(NoLineBreakPointException::class)
    override fun findNextBreakPoint(brlText: String, startPoint: Int, lineWrapCheck: Int): LineBreakResult {
        val maxLineLength = startPoint + lineWrapCheck
        var continuationDotsPos = -1
        if (maxLineLength >= brlText.length) {
            log.debug("No need for line wrapping as text is shorter than remaining line, inText \"{}\"", brlText)
            return LineBreakResult(brlText.length, null, -1)
        }
        var breakPoint: Int
        var outsideBreakPoint = brlText.length
        var outsideContinuationDotsPos = -1
        var outsideContinuationDots: String? = null
        var continuationDots: String? = null
        for ((p, curContDots) in matchPatterns) {
            breakPoint = -1
            var i = startPoint
            val m = p.matcher(brlText)
            log.debug("Testing pattern {}", m.pattern().pattern())
            while (i <= maxLineLength) {
                if (m.find(i)) {
                    i = m.end()
                    val contDotsEnd: Int = try {
                        m.end("contDots")
                    } catch (e: IllegalArgumentException) {
                        -1
                    }
                    val isInBoundBreak: Boolean = if (contDotsEnd >= 0) {
                        i <= maxLineLength - curContDots!!.length
                    } else {
                        i <= maxLineLength
                    }
                    log.debug("i={}, maxLineLength={}", i, maxLineLength)
                    if (isInBoundBreak) {
                        continuationDotsPos = contDotsEnd
                        continuationDots = curContDots
                        breakPoint = i
                    } else {
                        if (i < outsideBreakPoint) {
                            outsideContinuationDotsPos = contDotsEnd
                            outsideContinuationDots = curContDots
                            outsideBreakPoint = i
                        }
                        break
                    }
                    i++
                } else {
                    break
                }
            }
            if (breakPoint > startPoint) {
                log.debug("Found break point at {} in inText \"{}\"", breakPoint, brlText)
                return LineBreakResult(breakPoint, continuationDots, continuationDotsPos)
            }
        }
        log.debug("No line break inside preferred line length, next possible break point is {}", outsideBreakPoint)
        throw NoLineBreakPointException(
            "No possible line break was found for preferred length",
            LineBreakResult(outsideBreakPoint, outsideContinuationDots, outsideContinuationDotsPos)
        )
    }

    override fun checkStartLineInsertion(brlText: String, start: Int): InsertionResult {
        for ((p, value) in startLineInsertions) {
            var m = p.matcher(brlText)
            m = m.region(start, brlText.length)
            if (m.lookingAt()) {
                return InsertionResult(value, m.end())
            }
        }
        return InsertionResult(null, -1)
    }

    private val startLineInsertions: MutableMap<Pattern, String?> = LinkedHashMap()

    init {
        log.debug("Creating RegexLineWrapper with expressions {}", patterns)
        if (patterns != null) {
            for (patternStr in patterns) {
                matchPatterns[Pattern.compile(patternStr.matchPattern)] = patternStr.insertionDots
                log.debug("Added pattern \"{}\" to list of patterns", patternStr)
            }
        }
        // Add some fallback patterns
        matchPatterns[Pattern.compile(" ")] = null
        matchPatterns[Pattern.compile("\u00a0")] = null
        log.debug("Finished creating RegexLineWrapper")
    }

    fun setLineStartInsertions(insertions: Map<String, String>) {
        startLineInsertions.clear()
        for ((key, value) in insertions) {
            startLineInsertions[Pattern.compile(key)] = value
        }
    }

    val lineStartInsertions: Map<String, String?>
        get() {
            val result: MutableMap<String, String?> = LinkedHashMap()
            for ((key, value) in startLineInsertions) {
                result[key.pattern()] = value
            }
            return result
        }

    companion object {
        @JvmField
		val NEMETH_BREAK_POINTS = listOf(
            InsertionPatternEntry(" (?=(/?\\.k|\\.1|\"\\.) )", "\""),
            InsertionPatternEntry("(?=\\+|-|\\./|`\\*)", "\"")
        )
        @JvmField
		val UEB_MATH_BREAK_POINTS = listOf(
            InsertionPatternEntry("(?=\"7|`[<>])", "\""),
            InsertionPatternEntry("(?=\"[68-])", "\"")
        )
    }
}