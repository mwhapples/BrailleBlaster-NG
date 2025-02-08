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
package org.brailleblaster.perspectives.mvc.modules.misc

import nu.xom.Document
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.XMLNodeCaret.CursorPosition
import org.brailleblaster.perspectives.mvc.events.XMLCaretEvent
import org.brailleblaster.util.swt.EasySWT
import org.eclipse.swt.widgets.Shell
import org.slf4j.LoggerFactory

class TableSelectionModule(private val manager: Manager) : SimpleListener {
    var isTableSelected = false
        private set

    override fun onEvent(event: SimpleEvent) {
        if (event is XMLCaretEvent) {
            val selection = manager.simpleManager.currentSelection
            val blocks = selection.selectedBlocks
            val previouslyLocked = isTableSelected
            isTableSelected = false
            if (selection.start !== selection.end || selection.start.cursorPosition == CursorPosition.ALL) {
                for (block in blocks) {
                    if (isInsideTable(block)) {
                        isTableSelected = true
                        break
                    }
                }
            }
            if (previouslyLocked && !isTableSelected) {
                log.debug("No longer inside table")
            } else if (isTableSelected && !previouslyLocked) {
                log.debug("Now entering table")
            }
        }
    }

    companion object {
        const val TABLE_WARNING_DIALOG_TITLE = "Table Warning" //Public for use in tests
        private const val TABLE_WARNING_DIALOG =
            "This element is read-only because it is inside a table. To edit it, right-click and select \"Edit Table\"."
        private val log = LoggerFactory.getLogger(TableSelectionModule::class.java)
        fun isInsideTable(n: Node): Boolean {
            if (BBX.BLOCK.TABLE_CELL.isA(n) || BBX.CONTAINER.TABLE_ROW.isA(n) || BBX.CONTAINER.TABLE.isA(n)) return true
            if (BBX.CONTAINER.isA(n) || BBX.SECTION.isA(n) || n is Document) //Quick exit for any container that isn't a table row or table
                return false
            return BBX.CONTAINER.TABLE.isA((if (n is Text || BBX.SPAN.isA(n) || BBX.INLINE.isA(n)) {
                generateSequence(n) { it.parent }.takeWhile { !BBX.BLOCK.isA(it) }.last()
            } else n).parent) //Captions are blocks descended from a table container
        }

        @JvmStatic
		fun displayInvalidTableMessage(parent: Shell?) {
            EasySWT.makeEasyOkDialog(TABLE_WARNING_DIALOG_TITLE, TABLE_WARNING_DIALOG, parent)
        }
    }
}
