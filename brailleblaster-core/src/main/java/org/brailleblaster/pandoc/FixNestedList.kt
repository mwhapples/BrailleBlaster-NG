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
package org.brailleblaster.pandoc

import nu.xom.*
import kotlin.math.max

class FixNestedList : FixerInf {
    private var bbUri: String? = null
    private var rootElem: Element? = null
    override fun setFixer(fixer: Fixer) {
        bbUri = fixer.bbUri
        rootElem = fixer.rootElement
    }

    @Throws(Exception::class)
    override fun process() {
        unnest(rootElem)
    }

    // processes lists so that they are not nested and
    // adjacent lists are merged 
    @Throws(Exception::class)
    private fun unnest(elem: Element?) {
        var parent: ParentNode
        val node: Node?
        var listCtr = 0
        var isList: Boolean
        var isListItem: Boolean
        if (isList(elem)) {
            node = elem
            parent = node!!.parent
            isList = isList(parent)
            isListItem = isListItem(parent)
            while (isList || isListItem) {
                if (isList) {
                    listCtr++
                }
                parent = parent.parent
                isList = isList(parent)
                isListItem = isListItem(parent)
            }
            setListAttributes(elem, listCtr.toString())
        }
        val elems = elem!!.childElements
        for (i in 0 until elems.size()) {
            unnest(elems[i])
        }
        parent = elem.parent
        if (isListItem(parent)) {
            moveListItems(elem, parent)
        }
        if (isList(elem) &&
            !isListItem(parent)
        ) {
            checkAndMergeLists(elem, parent)
        }
    }

    private fun setListAttributes(e: Element?, level: String) {
        var block: Element
        var attr = e!!.getAttribute("listLevel", bbUri)
        attr.value = level
        val children = e.getChildElements("BLOCK", bbUri)
        if (null != children) {
            for (i in 0 until children.size()) {
                block = children[i]
                attr = block.getAttribute("itemLevel", bbUri)
                attr.value = level
            }
        }
    }

    private fun isList(p: Node?): Boolean {
        val elP: Element
        var isAList = false
        if (p is Element) {
            elP = p
            if (elP.localName.equals("CONTAINER", ignoreCase = true) &&
                elP.getAttributeValue("type", bbUri).equals("LIST", ignoreCase = true)
            ) {
                isAList = true
            }
        }
        return isAList
    }

    private fun isListItem(p: Node): Boolean {
        val elP: Element
        var isListItem = false
        if (p is Element) {
            elP = p
            if (elP.localName.equals("BLOCK", ignoreCase = true) &&
                elP.getAttributeValue("type", bbUri).equals("LIST_ITEM", ignoreCase = true)
            ) {
                isListItem = true
            }
        }
        return isListItem
    }

    // moves a nested list into the parent list
    private fun moveListItems(elem: Element?, parent: ParentNode) {
        var block: Element
        val listParent = parent.parent
        val index = listParent.indexOf(parent)
        val children = elem!!.getChildElements("BLOCK", bbUri)
        for (i in 0 until children.size()) {
            block = children[i]
            block.detach()
            listParent.insertChild(block, index + i + 1)
        }
        if (elem.childCount == 0) {
            parent.removeChild(elem)
            if (parent.childCount == 0) {
                listParent.removeChild(parent)
            }
        }
        setListLevel(listParent)
    }

    // determines the listLevel value for a list
    private fun setListLevel(list: Node) {
        val listEl: Element
        val children: Elements
        var child: Element
        var maxLevel: Int
        var level: Int
        var attr: Attribute? = null
        val maxAttr: Attribute?
        if (list is Element) {
            listEl = list
            maxAttr = listEl.getAttribute("listLevel", bbUri)
            maxLevel = maxAttr.value.toInt()
            children = listEl.childElements
            for (i in 0 until children.size()) {
                child = children[i]
                if (isListItem(child)) {
                    attr = child.getAttribute("itemLevel", bbUri)
                } else if (isList(child)) {
                    attr = child.getAttribute("listLevel", bbUri)
                }
                level = attr!!.value.toInt()
                if (maxLevel < level) maxLevel = level
            }
            maxAttr.value = maxLevel.toString()
        }
    }

    // this method merges list2 into list1
    private fun mergeLists(l1: Element, l2: Element?) {
        val maxLev: Int
        var child: Element
        val children = l2!!.getChildElements("BLOCK", bbUri)
        for (i in 0 until children.size()) {
            child = children[i]
            child.detach()
            l1.appendChild(children[i])
        }
        val lev1: Int = l1.getAttributeValue("listLevel", bbUri).toInt()
        val lev2: Int = l2.getAttributeValue("listLevel", bbUri).toInt()
        maxLev = max(lev2, lev1)
        l1.getAttribute("listLevel", bbUri).value = maxLev.toString()
        l2.parent.removeChild(l2)
    }

    // this method checks to see if lists are adjacent to one another
    // and if so, the lists are merged
    private fun checkAndMergeLists(elem: Element?, parent: ParentNode) {
        val index = parent.indexOf(elem)
        var list: Element
        var node: Node
        var k: Int
        var n: Int
        for (i in 0 until index) {
            node = parent.getChild(i)
            k = parent.indexOf(node)
            if (isList(node)) {
                list = node as Element
                n = 0
                for (j in k + 1 until index) {
                    node = parent.getChild(j)
                    n += if (node is Text) {
                        node.value.trim { it <= ' ' }.length
                    } else {
                        1
                    }
                }
                if (n == 0) {
                    mergeLists(list, elem)
                }
            }
        }
    }
}