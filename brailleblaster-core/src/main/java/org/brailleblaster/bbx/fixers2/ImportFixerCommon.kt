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
package org.brailleblaster.bbx.fixers2

import nu.xom.Element
import nu.xom.Node
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.document.BBDocument
import org.brailleblaster.settings.UTDManager
import org.brailleblaster.utd.UTDTranslationEngine
import org.brailleblaster.utd.internal.xml.FastXPath

object ImportFixerCommon {
    val UTD_MANAGER: UTDManager = BBDocument.TEST_UTD.let { testUtd ->
        if (BBDocument.TEST_MODE && testUtd != null) {
            testUtd
        } else {
            val tmpDoc = BBX.newDocument()
            tmpDoc.rootElement.appendChild(BBX.SECTION.ROOT.create())
            UTDManager().also {
                it.loadEngineFromDoc(tmpDoc, "bbx")
            }
        }
    }
    val UTD_ENGINE: UTDTranslationEngine = UTD_MANAGER.engine

    @SafeVarargs
    fun applyToDescendantBlocks(root: Element?, vararg onBlock: ApplyToDescendantBlocks) {
        val nodesToDetach: MutableList<Node> = mutableListOf()
        for (descendantNode in FastXPath.descendant(root)) {
            if (BBX.BLOCK.isA(descendantNode)) {
                val textChildren = FastXPath.descendant(descendantNode)
                    .filterIsInstance<Text>()
                    .toMutableList()
                for (curOnBlock in onBlock) {
                    if (nodesToDetach.contains(descendantNode)) {
                        break
                    }
                    curOnBlock.onBlock(descendantNode as Element, textChildren, nodesToDetach)
                }
            }
        }
        for (node in nodesToDetach) {
            node.detach()
        }
    }

    fun interface ApplyToDescendantBlocks {
        /**
         *
         * @param block
         * @param descendantTextNodes
         * @param nodesToDetach nodes to deatch afterwords as FastXPath doesn't support detaching in the middle of an iterator
         */
        fun onBlock(block: Element, descendantTextNodes: MutableList<Text>, nodesToDetach: MutableList<Node>)
    }
}