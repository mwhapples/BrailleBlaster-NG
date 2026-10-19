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
package org.brailleblaster.utd.tables

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.utd.ActionMap
import org.brailleblaster.utd.BRFWriter
import org.brailleblaster.utd.IStyle
import org.brailleblaster.utd.Style
import org.brailleblaster.utd.actions.GenericAction
import org.brailleblaster.utd.actions.IAction
import org.brailleblaster.utd.actions.SkipAction
import org.brailleblaster.utd.actions.TranslateTableAction
import org.brailleblaster.utd.internal.DynamicOptionStyleMap
import org.brailleblaster.utd.testutils.UTDTestUtils
import org.brailleblaster.utils.xml.UTD_NS
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.Test
import java.io.StringWriter

/**
 * Blank lines around a table count from the table's last line, so they merge
 * with the spacing of the block next to it instead of adding to it. The
 * pages have no running head, where an extra blank line used to show.
 */
class TableSpacingTest {
    @Test
    fun simpleTableHasOneBlankLineEachSide() {
        val pages = brf(HEADING + table(SIMPLE, BANA_SPACING, *ROWS) + body("Text"))

        assertBlankLinesBetween(pages, "Head", "zq  qz", 1)
        assertBlankLinesBetween(pages, "vv  ww", "Text", 1)
    }

    @Test
    fun listedTableHasOneBlankLineEachSide() {
        val pages = brf(HEADING + table(LISTED, BANA_SPACING, *ROWS) + body("Text"))

        assertBlankLinesBetween(pages, "Head", "zq3 zz", 1)
        assertBlankLinesBetween(pages, "qz3 ww", "Text", 1)
    }

    @Test
    fun simpleTableFallingBackToListedHasOneBlankLineEachSide() {
        val pages = brf(HEADING + table(SIMPLE, BANA_SPACING, *WIDE_ROWS) + body("Text"))

        assertBlankLinesBetween(pages, "Head", "zzzzzzzzzz3 zz", 1)
        assertBlankLinesBetween(pages, "qz qz qz qz zq", "Text", 1)
    }

    @Test
    fun headingsOnlyTableHasOneBlankLineAfter() {
        val pages = brf(HEADING + table(SIMPLE, BANA_SPACING, ROWS[0]) + body("Text"))

        assertBlankLinesBetween(pages, SEPARATOR, "Text", 1)
    }

    @Test
    fun listedTableKeepsBlankLineBetweenRows() {
        val pages = brf(HEADING + table(LISTED, NO_SPACING, *ROWS) + body("Text"))

        assertBlankLinesBetween(pages, "qz3 qq", "zq3 vv", 1)
    }

    @Test
    fun headingThenTableWithoutSpacing() {
        for (format in listOf(SIMPLE, LISTED)) {
            val pages = brf(HEADING + table(format, NO_SPACING, *ROWS) + body("Text"))

            assertBlankLinesBetween(pages, "Head", "zq", 0)
        }
        val pages = brf(HEADING + table(SIMPLE, NO_SPACING, *WIDE_ROWS) + body("Text"))
        assertBlankLinesBetween(pages, "Head", "zzzzzzzzzz3 zz", 0)
    }

    @Test
    fun tableWithoutSpacingThenHeadingTakesTheHeadingsLine() {
        for ((format, last) in listOf(SIMPLE to "vv  ww", LISTED to "qz3 ww")) {
            val pages = brf(HEADING + table(format, NO_SPACING, *ROWS) + NEXT_HEADING)

            assertBlankLinesBetween(pages, last, "Next", 1)
        }
        val pages = brf(HEADING + table(SIMPLE, NO_SPACING, *WIDE_ROWS) + NEXT_HEADING)
        assertBlankLinesBetween(pages, "qz qz qz qz zq", "Next", 1)
    }

    @Test
    fun tableWithoutSpacingNextToText() {
        for ((format, last) in listOf(SIMPLE to "vv  ww", LISTED to "qz3 ww")) {
            val pages = brf(body("Before") + table(format, NO_SPACING, *ROWS) + body("Text"))

            assertBlankLinesBetween(pages, "Before", "zq", 0)
            assertBlankLinesBetween(pages, last, "Text", 0)
        }
        val pages = brf(body("Before") + table(SIMPLE, NO_SPACING, ROWS[0]) + body("Text"))
        assertBlankLinesBetween(pages, SEPARATOR, "Text", 0)
    }

    @Test
    fun newPageBeforeTableStillBreaks() {
        val pages = brf(body("Before") + table(SIMPLE, "$NO_SPACING opt-newPagesBefore='1'", *ROWS))

        assertTrue(pages[0].none { "zq" in it }, "Table should leave the first page:\n${show(pages)}")
        assertTrue(pages[1].first { it.isNotBlank() }.startsWith("zq"), "Table should start page 2:\n${show(pages)}")
    }

    private fun brf(body: String): List<List<String>> {
        val doc = UTDTestUtils.generateBookDoc("", body)
        val engine = UTDTestUtils.initTestEngine(TableStyleMap(), TableActionMap())
        // AutoTableFormatter names the table's style
        listOf("Simple Table", "Listed Table").forEach { name ->
            engine.styleDefinitions.addStyle(Style().apply { this.name = name })
        }
        engine.translateAndFormatDocument(doc)
        val brf = StringWriter().also { engine.toBRF(doc, it) }.toString()
        return brf.split(BRFWriter.PAGE_SEPARATOR)
            .map { page -> page.lines().map { it.trimEnd() } }
            .filter { page -> page.any { it.isNotBlank() } }
    }

    /** Blank lines between the first line holding [before] and the next line holding [after], on one page. */
    private fun assertBlankLinesBetween(pages: List<List<String>>, before: String, after: String, blanks: Int) {
        val page = pages.firstOrNull { p -> p.any { before in it } }
            ?: throw AssertionError("No line with '$before':\n${show(pages)}")
        val from = page.indexOfFirst { before in it }
        val to = (from + 1 until page.size).firstOrNull { after in page[it] }
            ?: throw AssertionError("No line with '$after' after '$before':\n${show(pages)}")
        assertTrue(page.subList(from + 1, to).all { it.isBlank() }, "Text between '$before' and '$after':\n${show(pages)}")
        assertEquals(to - from - 1, blanks, "Blank lines between '$before' and '$after':\n${show(pages)}")
    }

    private fun show(pages: List<List<String>>) = pages.joinToString("\n----\n") { it.joinToString("\n") }

    /**
     * Style options from opt- attributes, leaving format to AutoTableFormatter.
     * A table's base style is a simple or listed table; rows and cells have
     * their own styles.
     */
    private class TableStyleMap : DynamicOptionStyleMap(null, null, "opt-", "dos-", "dos") {
        override fun findValue(node: Node): IStyle = when ((node as? Element)?.localName) {
            "tr" -> ROW
            "td" -> CELL
            else -> super.findValue(node)
        }

        override fun getBaseStyle(elem: Element): Style = when {
            elem.localName != "table" -> DEFAULT
            elem.getAttributeValue("format") == LISTED -> LISTED_TABLE
            else -> SIMPLE_TABLE
        }

        override fun onNewStyle(newStyle: Style) {}
    }

    /** Paragraphs are blocks translated as written; tables are translated as BBX translates them. */
    private class TableActionMap : ActionMap() {
        override fun findValue(node: Node): IAction = when {
            node is Element && node.localName == "p" -> UTDTestUtils.TestGenericBlockAction()
            node is Element && node.localName == "table" -> TranslateTableAction()
            node is Element && node.namespaceURI == UTD_NS -> SkipAction()
            else -> GenericAction()
        }
    }

    companion object {
        private const val SIMPLE = "simple"
        private const val LISTED = "listed"
        private const val BANA_SPACING = "opt-linesBefore='2' opt-linesAfter='2'"
        private const val NO_SPACING = "opt-linesBefore='1' opt-linesAfter='1'"

        private const val HEADING = "<p opt-linesBefore='2'>Head</p>"
        private const val NEXT_HEADING = "<p opt-linesBefore='2'>Next</p>"

        private val ROWS = arrayOf(listOf("zq", "qz"), listOf("zz", "qq"), listOf("vv", "ww"))
        private const val SEPARATOR = "\"3  \"3"

        // Too wide for a simple table, so it is laid out as a listed table
        private val WIDE_ROWS = arrayOf(
            listOf("zzzzzzzzzz", "qqqqqqqqqq", "zqzqzqzqzq", "qzqzqzqzqz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz qz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz zq")
        )

        private val DEFAULT = Style()
        private val SIMPLE_TABLE = Style().apply { setIsTable(true); setFormat(IStyle.Format.SIMPLE) }
        private val LISTED_TABLE = Style().apply { setIsTable(true); setFormat(IStyle.Format.LISTED) }
        private val ROW = Style().apply { setIsTableRow(true) }
        private val CELL = Style().apply { setIsTableCell(true) }

        private fun body(text: String) = "<p opt-linesBefore='1'>$text</p>"

        private fun table(format: String, options: String, vararg rows: List<String>) =
            "<table format='$format' $options>" + rows.joinToString("") { row ->
                "<tr>" + row.joinToString("") { "<td>$it</td>" } + "</tr>"
            } + "</table>"
    }
}
