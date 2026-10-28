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

import org.brailleblaster.utd.tables.TableTestUtils.BANA_SPACING
import org.brailleblaster.utd.tables.TableTestUtils.LISTED
import org.brailleblaster.utd.tables.TableTestUtils.NO_SPACING
import org.brailleblaster.utd.tables.TableTestUtils.SIMPLE
import org.brailleblaster.utd.tables.TableTestUtils.assertBlankLinesBetween
import org.brailleblaster.utd.tables.TableTestUtils.body
import org.brailleblaster.utd.tables.TableTestUtils.brf
import org.brailleblaster.utd.tables.TableTestUtils.show
import org.brailleblaster.utd.tables.TableTestUtils.table
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

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

        assertBlankLinesBetween(pages, HEADINGS_LINE, "Text", 1)
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
        assertBlankLinesBetween(pages, HEADINGS_LINE, "Text", 0)
    }

    @Test
    fun newPageBeforeTableStillBreaks() {
        val pages = brf(body("Before") + table(SIMPLE, "$NO_SPACING opt-newPagesBefore='1'", *ROWS))

        assertTrue(pages[0].none { "zq" in it }, "Table should leave the first page:\n${show(pages)}")
        assertTrue(pages[1].first { it.isNotBlank() }.startsWith("zq"), "Table should start page 2:\n${show(pages)}")
    }

    companion object {
        private const val HEADING = "<p opt-linesBefore='2'>Head</p>"
        private const val NEXT_HEADING = "<p opt-linesBefore='2'>Next</p>"

        private val ROWS = arrayOf(listOf("zq", "qz"), listOf("zz", "qq"), listOf("vv", "ww"))
        // A table of headings only is its line of headings, with no separation line
        private const val HEADINGS_LINE = "zq  qz"

        // Too wide for a simple table, so it is laid out as a listed table
        private val WIDE_ROWS = arrayOf(
            listOf("zzzzzzzzzz", "qqqqqqqqqq", "zqzqzqzqzq", "qzqzqzqzqz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz qz"),
            listOf("zz zz zz zz zz", "qq qq qq qq qq", "zq zq zq zq zq", "qz qz qz qz zq")
        )
    }
}
