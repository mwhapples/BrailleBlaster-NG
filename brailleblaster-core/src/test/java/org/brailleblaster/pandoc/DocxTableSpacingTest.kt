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
package org.brailleblaster.pandoc

import org.brailleblaster.archiver2.ArchiverFactory
import org.brailleblaster.bbx.BookToBBXConverter
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.BRFWriter
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.StringWriter

/**
 * Blank lines around Word tables match Duxbury, which ASM's statements are
 * made for: a table adds none of its own, a list next to it adds none, an
 * empty paragraph next to it adds none, and a heading keeps its spacing.
 */
class DocxTableSpacingTest {
    @BeforeClass
    fun init() {
        BookToBBXConverter.devSetup(arrayOf())
    }

    @Test
    fun headingThenTable() {
        val pages = brf(heading("Zebra") + simpleTable)

        assertBlankLinesBetween(pages, ",zebra", SIMPLE_HEADINGS, 0)
    }

    @Test
    fun headingEmptyParagraphTable() {
        val pages = brf(heading("Zebra") + EMPTY + simpleTable)

        assertBlankLinesBetween(pages, ",zebra", SIMPLE_HEADINGS, 0)
    }

    @Test
    fun headingEmptyParagraphListedTable() {
        val pages = brf(heading("Zebra") + EMPTY + listedTable)

        assertBlankLinesBetween(pages, ",zebra", LISTED_FIRST_LINE, 0)
    }

    @Test
    fun listedTableKeepsBlankLineBetweenRows() {
        val pages = brf(heading("Zebra") + EMPTY + listedTable + EMPTY + heading("Yak"))

        assertBlankLinesBetween(pages, LISTED_FIRST_ROW_END, LISTED_FIRST_LINE, 1)
    }

    @Test
    fun tableEmptyParagraphHeading() {
        val pages = brf(heading("Zebra") + simpleTable + EMPTY + heading("Yak"))

        assertBlankLinesBetween(pages, SIMPLE_LAST_ROW, ",yak", 1)
    }

    @Test
    fun listedTableEmptyParagraphHeading() {
        val pages = brf(heading("Zebra") + listedTable + EMPTY + heading("Yak"))

        assertBlankLinesBetween(pages, LISTED_LAST_LINE, ",yak", 1)
    }

    @Test
    fun tableEmptyParagraphText() {
        val pages = brf(heading("Zebra") + simpleTable + EMPTY + list("Quiz"))

        assertBlankLinesBetween(pages, SIMPLE_LAST_ROW, ",quiz", 0)
    }

    @Test
    fun listedTableEmptyParagraphText() {
        val pages = brf(heading("Zebra") + listedTable + EMPTY + list("Quiz"))

        assertBlankLinesBetween(pages, LISTED_LAST_LINE, ",quiz", 0)
    }

    @Test
    fun headingsOnlyTableEmptyParagraphText() {
        val pages = brf(heading("Zebra") + table(listOf("Zq", "Qz")) + EMPTY + list("Quiz"))

        assertBlankLinesBetween(pages, SIMPLE_SEPARATOR, ",quiz", 0)
    }

    @Test
    fun textEmptyParagraphTable() {
        val pages = brf(list("Quiz") + EMPTY + simpleTable)

        assertBlankLinesBetween(pages, ",quiz", SIMPLE_HEADINGS, 0)
    }

    @Test
    fun pageBreakBeforeTableStillBreaks() {
        val pages = brf(list("Quiz") + PAGE_BREAK + simpleTable)

        assertTrue(pages[0].none { SIMPLE_HEADINGS in it }, "Table should leave the first page:\n${show(pages)}")
        assertTrue(firstTextLine(pages[1]).startsWith(SIMPLE_HEADINGS), "Table should start the second page:\n${show(pages)}")
    }

    @Test
    fun pageBreakBeforeHeadingAndTableStillBreaks() {
        val pages = brf(list("Quiz") + PAGE_BREAK + heading("Zebra") + simpleTable)

        assertEquals(firstTextLine(pages[1]), ",zebra", "Heading should start the second page:\n${show(pages)}")
        assertBlankLinesBetween(pages, ",zebra", SIMPLE_HEADINGS, 0)
    }

    @Test
    fun emptyParagraphAwayFromTableUnchanged() {
        val pages = brf(list("Quiz") + EMPTY + list("Yak") + simpleTable)

        assertBlankLinesBetween(pages, ",quiz", ",yak", 0)
    }

    private fun brf(body: String): List<List<String>> {
        val docx = TestDocx.docx(body, styles = STYLES)
        val doc = ArchiverFactory.load(docx.toPath()).bbxDocument
        val utdManager = UTDManager()
        utdManager.loadEngineFromDoc(doc, "bbx")
        utdManager.engine.brailleSettings.isUseAsciiBraille = true
        val translated = utdManager.engine.translateAndFormatDocument(doc)
        val brf = StringWriter().also {
            utdManager.engine.toBRF(translated, it, 0, BRFWriter.EMPTY_PAGE_LISTENER)
        }.toString()
        return brf.split(BRFWriter.PAGE_SEPARATOR)
            .map { page -> page.lines().map { it.trimEnd().lowercase() } }
            .filter { page -> page.any { it.isNotBlank() } }
    }

    private fun firstTextLine(page: List<String>): String = page.first { it.isNotBlank() }.trim()

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

    private fun show(pages: List<List<String>>) =
        pages.joinToString("\n----\n") { it.joinToString("\n") }

    companion object {
        private const val EMPTY = "<w:p/>"
        private const val PAGE_BREAK = """<w:p><w:r><w:br w:type="page"/></w:r></w:p>"""

        private val STYLES = """
            <w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/></w:style>
            <w:style w:type="paragraph" w:styleId="List"><w:name w:val="List"/></w:style>"""

        private fun heading(text: String) = TestDocx.styledPara("Heading2", text)

        private fun list(text: String) = TestDocx.styledPara("List", text)

        private fun table(vararg rows: List<String>) =
            // ASM marks the first row as the heading row
            """<w:tbl><w:tblPr><w:tblLook w:firstRow="1"/></w:tblPr>""" + rows.joinToString("") { row ->
                "<w:tr>" + row.joinToString("") { "<w:tc>${TestDocx.para(it)}</w:tc>" } + "</w:tr>"
            } + "</w:tbl>"

        private val simpleTable = table(listOf("Zq", "Qz"), listOf("zz", "qq"), listOf("vv", "ww"))

        // Simple table headings, then the last row
        private const val SIMPLE_HEADINGS = ",zq"
        private const val SIMPLE_LAST_ROW = "vv"
        private const val SIMPLE_SEPARATOR = "\"33"

        // Too wide for a simple table, so it is laid out as a listed table
        private val listedTable = table(
            listOf("Zzzzzzzzzz", "Qqqqqqqqqq", "Zqzqzqzqzq", "Qzqzqzqzqz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz qz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz zq")
        )
        private const val LISTED_FIRST_LINE = ",zzzzzzzzzz"
        private const val LISTED_FIRST_ROW_END = "qz qz qz qz qz"
        private const val LISTED_LAST_LINE = "qz qz qz qz zq"
    }
}
