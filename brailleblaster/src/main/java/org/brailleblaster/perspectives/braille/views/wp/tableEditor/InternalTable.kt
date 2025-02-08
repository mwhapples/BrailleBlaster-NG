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
package org.brailleblaster.perspectives.braille.views.wp.tableEditor

import nu.xom.Element
import nu.xom.Node

internal class InternalTable(
    override val nodes: MutableList<MutableList<Node>>,
    override val captions: List<Element>,
    override var tNContainer: Element?,
    override var type: TableType
) : ITable {
    override var displayedRows: Int = rows
        set(value) {
            field = value
            if (value > rows) {
                rows = value
            }
        }
    override var displayedCols: Int = cols
        set(value) {
            field = value
            if (value > cols) {
                cols = value
            }
        }

    override val displayedNodes: List<MutableList<Node>>
        get() {
            val returnList: MutableList<MutableList<Node>> = ArrayList()
            var i = 0
            while (i < nodes.size && i < displayedRows) {
                returnList.add(ArrayList())
                var j = 0
                while (j < nodes[i].size && j < displayedCols) {
                    returnList[returnList.size - 1].add(nodes[i][j])
                    j++
                }
                i++
            }
            return returnList
        }
}
