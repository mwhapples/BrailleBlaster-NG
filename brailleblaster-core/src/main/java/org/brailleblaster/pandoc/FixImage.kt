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

import nu.xom.Element
import nu.xom.Node
import nu.xom.ParentNode
import nu.xom.Text

class FixImage : FixerInf {
    private var bbUri: String? = null
    private var rootElem: Element? = null
    private var topNode: Node? = null
    override fun process() {
        val noText = ArrayList<Node>()
        val findFirstText = true
        val firstTextBlock: Node? = null
        fixImages(topNode, findFirstText, noText, firstTextBlock)
    }

    override fun setFixer(fixer: Fixer) {
        bbUri = fixer.bbUri
        rootElem = fixer.rootElement
        topNode = fixer.topNode
    }

    private fun fixImages(
            z: Node?, fft: Boolean,
            noText: ArrayList<Node>,
            ftb: Node?
    ) {
        var findFirstText = fft
        var firstTextBlock = ftb
        if (null == z) return
        var pInd: Int
        var lName: String?
        var e: Element?
        var p: ParentNode?
        var n: Node?
        var k: Node?
        var move = false
        var added: Boolean
        val bwt = isBlockWithText(z)
        val bnt = isEmptyBlock(z)
        val zChildCount = z.childCount
        var m: Int
        for (i in 0 until zChildCount) {
            added = false
            n = z.getChild(i)
            p = z as ParentNode?
            pInd = p!!.parent.indexOf(p) + 1
            // check for first text node
            if (isBlockWithText(n) && findFirstText) {
                firstTextBlock = n
                m = noText.size
                if (m > 0) {
                    var j = m - 1
                    while (0 <= j) {
                        k = noText[j]
                        k.detach()
                        p.insertChild(k, p.indexOf(n) + 1)
                        j--
                    }
                }
                findFirstText = false
            }
            // check for IMAGE elements
            if (n is Element) {
                e = n
                lName = e.localName
                if (lName.equals("IMAGE", ignoreCase = true)) {
                    e.localName = "CONTAINER"
                    move = false
                    // if image before first text
                    if (firstTextBlock == null && findFirstText) {
                        noText.add(n)
                        added = true
                    }
                    // check to see if parent is empty text block
                    if (bnt) {
                        move = true
                    }
                    if (bwt) {
                        e.localName = "SPAN"
                    }
                }
                // move 
                if (move && !added) {
                    n.detach()
                    p = p.parent
                    var blank = isEmptyBlock(p)
                    while (blank) {
                        pInd = p!!.parent.indexOf(p) + 1
                        p = p.parent
                        blank = isEmptyBlock(p)
                    }
                    p!!.insertChild(n, pInd)
                }
            }
            fixImages(n, findFirstText, noText, firstTextBlock)
        }
    }

    // a method to determine if a BLOCK has TEXT
    private fun isBlockWithText(n: Node?): Boolean {
        var itIs = false
        if (n is Element && n.localName.equals("BLOCK", ignoreCase = true)) {
            var m: Int
            for (i in 0 until n.childCount) {
                val k = n.getChild(i)
                if (k is Text) {
                    m = k.value.trim { it <= ' ' }.length
                    if (m > 0) {
                        itIs = true
                        break
                    }
                } else if (k is Element) {
                    if (k.localName.equals("INLINE", ignoreCase = true)) {
                        if (k.value.trim { it <= ' ' }.isNotEmpty()) {
                            itIs = true
                            break
                        }
                    }
                }
            }
        }
        return itIs
    }

    // a method to determine if a BLOCK is empty
    private fun isEmptyBlock(n: Node?): Boolean {
        var itIs = false

        if (n is Element && n.localName.equals("BLOCK", ignoreCase = true)) {
            var m: Int
            itIs = true
            for (i in 0 until n.getChildCount()) {
                val k = n.getChild(i)
                if (k is Text) {
                    m = k.value.trim { it <= ' ' }.length
                    if (m > 0) {
                        itIs = false
                        break
                    }
                } else if (k is Element) {
                    if (k.localName.equals("INLINE", ignoreCase = true)) {
                        if (k.value.trim { it <= ' ' }.isNotEmpty()) {
                            itIs = false
                            break
                        }
                    }
                }
            }
        }
        return itIs
    }
}