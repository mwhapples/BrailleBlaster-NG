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
package org.brailleblaster.utd.actions

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan

class RemoveLineAttributeAction : IBlockAction {
    private val action = GenericBlockAction()

    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        if (node is Element) {
            return processNode(node, context)
        }
        return emptyList()
    }

    private fun processNode(element: Element, engine: ITranslationEngine?): List<TextSpan> {
        val verifiedNode = verifyNode(element)

        if (verifiedNode != null) {
            val linenum = verifiedNode.getAttribute("linenum")
            verifiedNode.removeAttribute(linenum)

            //Only translates the line itself
            return ArrayList(action.applyTo(verifiedNode, engine!!))
        } else {
            return emptyList()
        }
    }

    private fun verifyNode(node: Element): Element? {
        if (node.getAttribute("linenum") != null) {
            return node
        }


        //Else go through the children to find the attribute
        for (i in 0 until node.childCount) {
            if (node.getChild(i) is Element) {
                return verifyNode(node.getChild(i) as Element)
            }
        }

        return null
    }
}
