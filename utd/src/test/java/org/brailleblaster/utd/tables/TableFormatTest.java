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
package org.brailleblaster.utd.tables;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class TableFormatTest {
    @Test
    public void testFormatter() {
        TableFormat simple = TableFormat.SIMPLE;
        TableFormat stairstep = TableFormat.STAIRSTEP;
        TableFormat listed = TableFormat.LISTED;
        TableFormat linear = TableFormat.LINEAR;
        TableFormat auto = TableFormat.AUTO;
        assertTrue(simple.formatter instanceof SimpleTableFormatter);
        assertTrue(stairstep.formatter instanceof StairstepTableFormatter);
        assertTrue(listed.formatter instanceof ListedTableFormatter);
        assertTrue(linear.formatter instanceof LinearTableFormatter);
        assertTrue(auto.formatter instanceof AutoTableFormatter);
    }
}
