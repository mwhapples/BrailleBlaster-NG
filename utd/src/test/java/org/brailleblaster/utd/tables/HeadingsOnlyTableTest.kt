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

import org.brailleblaster.utd.tables.TableTestUtils.NO_SPACING
import org.brailleblaster.utd.tables.TableTestUtils.SIMPLE
import org.brailleblaster.utd.tables.TableTestUtils.body
import org.brailleblaster.utd.tables.TableTestUtils.brf
import org.brailleblaster.utd.tables.TableTestUtils.show
import org.brailleblaster.utd.tables.TableTestUtils.table
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

/**
 * A table with only its heading row, like an LWS transaction table with no
 * transactions: one line of headings two spaces apart when they fit, or else
 * one listed entry, the first heading, a colon and the others separated by
 * semicolons, at cell 1 with runovers at cell 3.
 */
class HeadingsOnlyTableTest {
    @Test
    fun wideHeadingsAreOneListedEntry() {
        val lines = lines(table(SIMPLE, NO_SPACING, WIDE_HEADINGS) + body("Text"))

        assertEquals(lines.take(2), listOf("zzzzzzzzzz3 qqqqqqqqqq2 zqzqzqzqzq2", "  qzqzqzqzqz"), lines.joinToString("\n"))
    }

    @Test
    fun headingsThatFitAreOneLine() {
        val lines = lines(table(SIMPLE, NO_SPACING, listOf("zq zq", "qz", "vv")) + body("Text"))

        assertEquals(lines.take(2), listOf("zq zq  qz  vv", "Text"), lines.joinToString("\n"))
    }

    @Test
    fun headingsAndOneRowUnchanged() {
        val lines = lines(table(SIMPLE, NO_SPACING, listOf("zq zq", "qz"), listOf("zz", "qq")) + body("Text"))

        assertEquals(lines.take(3), listOf("zq zq  qz", "\"3333  \"3", "zz \"\"  qq"), lines.joinToString("\n"))
    }

    private fun lines(body: String): List<String> {
        val pages = brf(body)
        assertTrue(pages.size == 1, show(pages))
        return pages[0].filter { it.isNotBlank() }
    }

    companion object {
        // 46 cells with two spaces between, too wide for a 40-cell line
        private val WIDE_HEADINGS = listOf("zzzzzzzzzz", "qqqqqqqqqq", "zqzqzqzqzq", "qzqzqzqzqz")
    }
}
