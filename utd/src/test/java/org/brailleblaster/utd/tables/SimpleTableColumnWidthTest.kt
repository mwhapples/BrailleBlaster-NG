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
import org.brailleblaster.utd.tables.TableTestUtils.brf
import org.brailleblaster.utd.tables.TableTestUtils.show
import org.brailleblaster.utd.tables.TableTestUtils.table
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

/**
 * Braille Formats 11.2.5.c: a simple table's column is as wide as its widest
 * entry, and when the columns don't fit, no wider than its heading and
 * entries need on two lines. The separation line is as wide as the column.
 */
class SimpleTableColumnWidthTest {
    @Test
    fun wrappingEntryGetsNoSpareCells() {
        // 13 + 17 + 11 cells don't fit; the middle entry wraps at any width short of 17,
        // so the column is as wide as its heading
        val lines = lines(
            listOf("zq", "qqqqqqqqqq", "qz"),
            listOf("zzzzzzzzzzzzz", "zqz zqzqz zqzqzqz", "vzvzvzvzvzv")
        )

        assertEquals(
            lines,
            listOf(
                "zq             qqqqqqqqqq  qz",
                "\"333333333333  \"333333333  \"3333333333",
                "zzzzzzzzzzzzz  zqz zqzqz   vzvzvzvzvzv",
                "                 zqzqzqz",
            ),
            lines.joinToString("\n")
        )
    }

    @Test
    fun headingDividedSoColumnIsNarrowest() {
        // The last column's entries need 9 cells, so its heading divides after 8
        val lines = lines(
            listOf("", "zqz zqz zqzqz", "qzq qzqz zq zqzq"),
            listOf("zqzqzqzqzqzqzq", "qqqqqqq", "zzzzzzzzz")
        )

        assertEquals(
            lines,
            listOf(
                "                zqz zqz  qzq qzqz",
                "                zqzqz    zq zqzq",
                "                \"333333  \"33333333",
                "zqzqzqzqzqzqzq  qqqqqqq  zzzzzzzzz",
            ),
            lines.joinToString("\n")
        )
    }

    @Test
    fun tableThatFitsUnchanged() {
        val lines = lines(listOf("zq", "qz"), listOf("zzz", "qq"))

        assertEquals(lines, listOf("zq   qz", "\"33  \"3", "zzz  qq"), lines.joinToString("\n"))
    }

    // The table's lines, without the page number
    private fun lines(vararg rows: List<String>): List<String> {
        val pages = brf(table(SIMPLE, NO_SPACING, *rows))
        assertTrue(pages.size == 1, show(pages))
        return pages[0].filter { it.isNotBlank() && !it.trim().startsWith("#") }
    }
}
