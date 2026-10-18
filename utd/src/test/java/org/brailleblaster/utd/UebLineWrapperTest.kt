/*
 * Copyright (C) 2026 American Printing House for the Blind
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

import org.brailleblaster.utd.properties.UTDElements
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNull
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

/**
 * UEB divides an unspaced sequence longer than a line with the line
 * continuation indicator (dot 5, ASCII ") at the end of each line.
 */
class UebLineWrapperTest {
    private val wrapper = UebLineWrapper(asciiBraille = true)

    private fun division(word: String, room: Int): String? =
        wrapper.divideLongWord(word, room)?.let { word.substring(0, it.lineBreakPosition) + it.insertionDots }

    /** The text lines of the first page after adding [text], lower case, without the page number line. */
    private fun layout(text: String, table: String = "en-ueb-g2.ctb"): List<String> {
        val engine = UTDTranslationEngine().apply {
            brailleSettings = BrailleSettings(mainTranslationTable = table, isUseAsciiBraille = true)
        }
        val pb = PageBuilder(engine, Cursor()).apply {
            setFirstLineIndent(0)
            setLeftIndent(0)
        }
        pb.addBrl(UTDElements.BRL.create().apply { appendChild(text) })
        return pb.toBRF().lowercase().split(System.lineSeparator(), "\u000c")
            .map { it.trimEnd() }
            .filter { it.isNotEmpty() && !it.trim().matches(Regex("#[a-j]+")) }
    }

    private fun assertFitsGrid(lines: List<String>) =
        lines.forEach { assertTrue(it.length <= 40, "Line longer than 40 cells: '$it'") }

    /** Joins lines that end in the continuation indicator back to the next line. */
    private fun joined(lines: List<String>): String =
        lines.joinToString(" ").replace("\" ", "")

    @Test
    fun dividesAfterSlashOrBeforeDot() {
        assertEquals(division("http3_/_/www4hud4gov_/(fices_/hsg_/sfh_/hcc_/hcs4cfm", 40), "http3_/_/www4hud4gov_/(fices_/hsg_/\"")
        // before the dot, not between "bank" and the "ing" contraction
        assertEquals(division("https3_/_/www4pnc4com_/5_/p]sonal-bank+4html", 40), "https3_/_/www4pnc4com_/5_/p]sonal-bank+\"")
    }

    @Test
    fun neverSplitsAPrefixOrANumber() {
        // no natural point: the last division that keeps the capital sign with its letter
        assertEquals(division("abcdefghi,jklmnop", 11), "abcdefghi\"")
        // the number #abc keeps its number sign and digits together
        assertEquals(division("xyzxyz#abcdefg", 10), "xyzxyz\"")
    }

    @Test
    fun wordThatFitsIsNotDivided() {
        assertNull(wrapper.divideLongWord("pnc4com_/automat\$pay=m4 ", 23))
    }

    @Test
    fun spacedTextIsNotDivided() {
        assertNull(wrapper.divideLongWord("some spaced text that runs past the end of the line", 40))
        val unicode = UebLineWrapper(asciiBraille = false)
        val cells = String(CharArray(50) { Char(0x2801 + it % 2) })
        assertNull(unicode.divideLongWord(cells.substring(0, 20) + Char(0x2800) + cells.substring(20), 40))
        assertEquals(unicode.divideLongWord(cells, 40)?.insertionDots, Char(0x2810).toString())
    }

    @Test
    fun longUrlDividedWithIndicator() {
        val lines = layout("f miss+ a pay;t4 ,visit https3_/_/www4pnc4com_/5_/p]sonal-bank+4html for m\"o")

        assertEquals(lines[1], "https3_/_/www4pnc4com_/5_/p]sonal-bank+\"")
        assertEquals(lines[2], "4html for m\"o")
        assertFitsGrid(lines)
    }

    @Test
    fun stringThatFitsOnNextLineMovesWhole() {
        val lines = layout("${"a".repeat(30)} pnc4com_/automat\$pay=m4 ,mail")

        assertEquals(lines[1], "pnc4com_/automat\$pay=m4 ,mail")
        assertTrue(lines.none { it.endsWith("\"") }, "Unexpected continuation indicator: $lines")
    }

    @Test
    fun urlOverThreeLines() {
        val url = "http3_/_/www4hud4gov_/" + "(fices_/hsg_/sfh_/hcc_/".repeat(4) + "hcs4cfm"
        val lines = layout("go to $url or call")

        assertTrue(lines.size >= 4, "Expected the URL on 3 or more lines: $lines")
        lines.drop(1).dropLast(1).forEach { assertTrue(it.endsWith("\""), "Division without the indicator: $lines") }
        assertEquals(joined(lines), "go to $url or call")
        assertFitsGrid(lines)
    }

    @Test
    fun otherCodesStillCutWithoutIndicator() {
        val url = "https3_/_/www4pnc4com_/5_/p]sonal-bank+4html"
        val lines = layout("visit $url", table = "compress.cti,en-us-g2.ctb")

        assertEquals(lines[1] + lines[2], url)
        assertTrue(lines.none { it.endsWith("\"") }, "Unexpected continuation indicator: $lines")
    }
}
