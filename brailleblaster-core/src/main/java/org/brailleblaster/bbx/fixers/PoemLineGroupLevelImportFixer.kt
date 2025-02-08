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
package org.brailleblaster.bbx.fixers

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.internal.xml.FastXPath
import org.brailleblaster.utd.matchers.INodeMatcher

@Suppress("UNUSED")
class PoemLineGroupLevelImportFixer : AbstractFixer() {
    override fun fix(matchedNode: Node) {
        BBX.BLOCK.LIST_ITEM.assertIsA(matchedNode)
        val il = getItemLevel(matchedNode)
            ?: throw IllegalArgumentException("Not a suitable node")
        val elem = matchedNode as Element
        BBX.BLOCK.LIST_ITEM.ATTRIB_ITEM_LEVEL[elem] = il

        //Adjust runover
//		Element highestList = null;
//		for (Element curAncestor : FastXPath.ancestor(elem)) {
//			if (BBX.CONTAINER.LIST.isA(curAncestor)) {
//				BBX.ListType type = BBX.CONTAINER.LIST.ATTRIB_LIST_TYPE.get(curAncestor);
//				if (type == BBX.ListType.POEM || type == BBX.ListType.POEM_LINE_GROUP) {
//					highestList = curAncestor;
//				}
//			}
//		}
//		BBX.CONTAINER.LIST.ATTRIB_LIST_LEVEL.set(highestList, existing -> existing < itemLevel ? itemLevel : existing);
    }

    //	private static Element getHighestList(Node cursor) {
    //		
    //	}
    class BadLevelMatcher : INodeMatcher {
        override fun isMatch(node: Node, namespaces: NamespaceMap): Boolean {
            val expectedLevel = getItemLevel(node) ?: return false
            return BBX.BLOCK.LIST_ITEM.ATTRIB_ITEM_LEVEL[node as Element] != expectedLevel
        }
    }

    companion object {
        private fun getItemLevel(node: Node): Int? {
            if (!BBX.BLOCK.LIST_ITEM.isA(node)) {
                return null
            }

            // Issue #5577: Start at -1 so first found poem line group starts at level 0
            var level = -1
            var found = false
            for (curAncestor in FastXPath.ancestor(node)) {
                if (BBX.CONTAINER.LIST.isA(curAncestor)
                    && BBX.CONTAINER.LIST.ATTRIB_LIST_TYPE[curAncestor] == BBX.ListType.POEM_LINE_GROUP
                ) {
                    found = true
                    level++
                }
            }
            return if (!found) {
                null
            } else level
        }
    }
}