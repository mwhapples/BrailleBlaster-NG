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
package org.brailleblaster.perspectives.braille.mapping.elements

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.exceptions.OutdatedMapListException
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.interfaces.Uneditable
import org.brailleblaster.perspectives.mvc.modules.misc.TableSelectionModule.Companion.displayInvalidTableMessage
import java.util.*
import kotlin.math.max

class TableTextMapElement : TextMapElement, Uneditable {
    @JvmField
    val tableElements: MutableList<TableCellTextMapElement>
    lateinit var columns: Array<TableColumn>
    var startingVPos: Double = 0.0
        private set
    var endingVPos: Double = 0.0
        private set

    constructor(start: Int, end: Int, n: Node?) : super(start, end, n) {
        tableElements = ArrayList()
        isReadOnly = true
    }

    constructor(n: Node?) : super(n) {
        tableElements = ArrayList()
        isReadOnly = true
    }

    val tableType: String
        get() {
            if ((node as Element).getAttribute("class") != null) {
                val tableClass = (node as Element).getAttributeValue("class")
                if (tableClass.contains("utd:table")) {
                    return tableClass.substring(9).lowercase(Locale.getDefault())
                }
            }
            return ""
        }

    fun getCellsInRow(row: Int): List<TableCellTextMapElement> {
        val returnList: MutableList<TableCellTextMapElement> = ArrayList()
        var foundRowFlag = false
        for (cell in tableElements) {
            if (cell.row == row) {
                returnList.add(cell)
                foundRowFlag = true
            } else if (foundRowFlag) break
        }
        return returnList
    }

    fun getCellsInCol(col: Int): List<TableCellTextMapElement> {
        val returnList: MutableList<TableCellTextMapElement> = ArrayList()
        for (cell in tableElements) {
            if (cell.col == col) {
                returnList.add(cell)
            }
        }
        return returnList
    }

    fun setLines(m: Manager?) {
        val totalCols = findTotalCols()
        columns = Array(totalCols) { TableColumn() }.apply {
            lastOrNull()?.isLastColumn = true
        }
        for (tableElement in tableElements) {
            val curColumn = columns[tableElement.col]
            curColumn.findBrlWidth(tableElement)
        }

        var startVPos = 0.0
        for (j in tableElements.indices) {
            val column = columns[tableElements[j].col]
            tableElements[j].splitIntoLines(m!!, column.brlWidth, column.isLastColumn)
            if (j == 0) startVPos = findEarliestVPos()
            if (j == tableElements.size - 1) setStartAndEndVPos(startVPos, tableElements[j].endingVPos)
        }

        for (i in columns.indices) {
            columns[i].findPrintWidth(i)
        }
    }

    inner class TableColumn {
        var isLastColumn: Boolean = false
        var brlWidth: Int = -1
        var printWidth: Int = -1

        fun findPrintWidth(colNum: Int) {
            var cellTotal = 0
            var prevRow = -1
            var prevCol = -1
            for (i in tableElements.indices) {
                val curCell = tableElements[i]
                if (curCell.col != colNum) continue
                for (value in curCell.lines.values) {
                    cellTotal = max(cellTotal.toDouble(), (value as String).length.toDouble())
                        .toInt()
                }
                if (prevRow == curCell.row && prevCol == curCell.col) {
                    var prevLength = 0
                    var thisLength = 0
                    val prevCell = tableElements[i - 1]
                    if (!prevCell.isImage(prevCell.lines) && !curCell.isImage(curCell.lines)) {
                        val lastLine = prevCell.lines[prevCell.endingVPos] as String?
                        val thisLine = curCell.lines[prevCell.endingVPos] as String?
                        if (!lastLine.isNullOrEmpty() && !thisLine.isNullOrEmpty()) {
                            cellTotal = max(cellTotal.toDouble(), (lastLine.length + thisLine.length).toDouble())
                                .toInt()
                        }
                    } else {
                        if (prevCell.isImage(prevCell.lines)) {
                            prevLength = 1
                        } else {
                            if (prevCell.lines[prevCell.endingVPos] != null) prevLength =
                                (prevCell.lines[prevCell.endingVPos] as String?)!!.length
                        }
                        if (curCell.isImage(curCell.lines)) {
                            thisLength = 1
                        } else {
                            if (curCell.lines[curCell.endingVPos] != null) thisLength =
                                (curCell.lines[curCell.endingVPos] as String?)!!.length
                        }
                        if (thisLength != 0 && prevLength != 0) {
                            cellTotal = max(cellTotal.toDouble(), (prevLength + thisLength).toDouble()).toInt()
                        }
                    }
                }
                prevRow = curCell.row
                prevCol = curCell.col
            }
            printWidth = cellTotal
        }

        fun findBrlWidth(cell: TableCellTextMapElement) {
            for (i in cell.brailleList.indices) {
                if (cell.brailleList[i].node.value.contains("\"3")) {
                    brlWidth = cell.brailleList[i].node.value.length
                }
            }
        }
    }

    private fun findTotalCols(): Int {
        var totalCols = 0
        for (tableElement in tableElements) {
            totalCols = max(totalCols.toDouble(), tableElement.col.toDouble()).toInt()
        }
        return totalCols + 1
    }

    fun findEarliestVPos(): Double {
        val firstRow = getCellsInRow(0)
        if (firstRow.isEmpty()) return 0.0
        var start = -1.0
        for (cell in firstRow) {
            for (i in cell.brailleList.indices) {
                if (cell.brailleList[i] !is BrlOnlyBrlMapElement
                    && cell.brailleList[i] !is NewPageBrlMapElement
                    && cell.brailleList[i] !is BraillePageBrlMapElement
                    && cell.brailleList[i] !is PrintPageBrlMapElement
                ) {
                    if (cell.brailleList[i].vPos != null
                        && (cell.brailleList[i].vPos < start || start == -1.0)
                    ) {
                        start = cell.brailleList[i].vPos
                    }
                    break
                }
            }
        }
        if (start < 0) return 0.0
        return start
    }

    fun setEarliestOffset(pos: Int) {
        if (tableElements.isNotEmpty()) {
            if (!tableElements[0].brailleList.isEmpty()) {
                tableElements[0].brailleList[0].setOffsets(pos, pos)
            }
        }
    }

    fun setLatestOffset(pos: Int) {
        if (tableElements.isNotEmpty() && !tableElements[tableElements.size - 1].brailleList.isEmpty())
            tableElements[tableElements.size - 1].brailleList.last().setOffsets(pos, pos)
    }

    fun setStartAndEndVPos(start: Double, end: Double) {
        startingVPos = start
        endingVPos = end
    }

    override fun toString(): String {
        val returnString = StringBuilder()
        returnString.append("Node: ").append(node.toXML()).append("\nTableElements:")
        for (tableElement in tableElements) {
            returnString.append("\n").append(tableElement.node.toXML()).append("\n\tBrailleList: ")
            for (bme in tableElement.brailleList) {
                returnString.append("\n\t\t").append(bme.node.toXML())
            }
            returnString.append("\n\tLines: ").append(tableElement.lines)
        }
        return returnString.toString()
    }

    override fun getNodeParent(): Element {
        if (node.parent == null) {
            throw OutdatedMapListException("Table has no parent")
        }
        val p = node.parent
        val index = p.indexOf(node)
        return p.getChild(index - 1) as Element
    }

    override fun blockEdit(m: Manager) {
        displayInvalidTableMessage(m.wpManager.shell)
    }
}
