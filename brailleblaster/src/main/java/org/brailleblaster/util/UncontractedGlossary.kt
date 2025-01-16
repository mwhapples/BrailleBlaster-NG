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
package org.brailleblaster.util

import nu.xom.*
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.BBXUtils
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.utd.properties.UTDElements

object UncontractedGlossary {
    private var selectedNode: Node? = null
    private var guideWords: MutableList<Element?> = ArrayList()
    fun addUncontractedWord(e: BBSelectionData) {
        selectedNode = e.manager.simpleManager.currentSelection.start.node
        var toRefresh = selectedNode!!.parent as Element
        /*
     * 	Find guide word
     * 	Find the element with a utd-style="Guide Word"
     */

        //Get start and end element and all the nodes in between
        val startNode = e.manager.simpleManager.currentSelection.start.node.parent as Element
        val endNode = e.manager.simpleManager.currentSelection.end.node.parent as Element
        val followingStart = startNode.query("following::node()")
        val precedingEnd = endNode.query("preceding::node()")
        if (startNode !== endNode) {
            toRefresh = BBXUtils.getCommonParent(startNode, endNode) as Element
        }
        if (selectedNode is Text) {
            if (startNode !== endNode) {
                if (isGuideWord(startNode)) {
                    guideWords.add(startNode)
                }
                for (i in 0 until followingStart.size()) {
                    if (precedingEnd.contains(followingStart[i])
                        && followingStart[i] is Element
                    ) {
                        if (isGuideWord(followingStart[i] as Element)) {
                            guideWords.add(followingStart[i] as Element)
                        }
                    }
                }
                if (isGuideWord(endNode)) {
                    guideWords.add(endNode)
                }
            } else {
                if (isGuideWord(startNode)) {
                    guideWords.add(startNode)
                }
            }
        } else if (selectedNode is Element) {
            getGuideWords(selectedNode as Element?)
        }

        //If you cannot find guide words, resort to finding the first words of the list
        if (guideWords.isEmpty()) {
            handleNonGuideWords(startNode, endNode, followingStart, precedingEnd)
        }
        pronounceGuideWords()
        e.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.NO_SENDER, true, toRefresh))
        guideWords = ArrayList()
    }

    private fun handleNonGuideWords(startNode: Node, endNode: Node, followingStart: Nodes, precedingEnd: Nodes) {
        findFirstWord(startNode)
        for (i in 0 until followingStart.size()) {
            if (precedingEnd.contains(followingStart[i])
                && followingStart[i] is Element
                && !UTDElements.BRL.isA(followingStart[i])
            ) {
                findFirstWord(followingStart[i])
            }
        }
        findFirstWord(endNode)
    }

    private fun findFirstWord(node: Node) {
        if (node is Text) {
            val span = BBX.SPAN.OTHER.create()
            val text = node.getValue()
            val firstWord = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val remainingText = if (firstWord.length == text.length) "" else text.substring(firstWord.length + 1)
            span.appendChild(firstWord)
            guideWords.add(span)

            //Replace the value of the selected node to the span + the rest of the previous text
            val parent = node.getParent() as Element
            parent.replaceChild(node, span)
            if (remainingText.isNotEmpty()) {
                parent.appendChild(remainingText)
            }
        } else if (node is Element) {
            //If the element has children, find child guide words
            for (i in 0 until node.getChildCount()) {
                val child = node.getChild(i)
                findFirstWord(child)
                if (child is Text) {
                    return
                }
            }
        }
    }

    private fun getGuideWords(element: Element?) {
        //Check if element is a guide word
        if (isGuideWord(element)) {
            guideWords.add(element)
        }

        //If the element has children, find child guide words
        for (i in 0 until element!!.childCount) {
            if (element.getChild(i) is Element) {
                getGuideWords(element.getChild(i) as Element)
            }
        }
    }

    private fun pronounceGuideWords() {
        for (e in guideWords) {
            if (e!!.getAttribute("pronunciation") != null) {
                e.removeAttribute(e.getAttribute("pronunciation"))
                e.removeAttribute(e.getAttribute("type"))
                e.removeAttribute(e.getAttribute("term"))
            } else {
                e.addAttribute(Attribute("type", "pronunciation"))
                e.addAttribute(Attribute("term", findFirstTextNode(e)!!.value))
                e.addAttribute(Attribute("pronunciation", "done"))
            }
        }
    }

    private fun isGuideWord(element: Element?): Boolean {
        val style = element!!.getAttribute("utd-style")
        return style != null && style.value == "Guide Word"
    }

    private fun findFirstTextNode(element: Element?): Node? {
        for (i in 0 until element!!.childCount) {
            if (element.getChild(i) is Text) {
                return element.getChild(i)
            } else if (element.getChild(i) is Element) {
                return findFirstTextNode(element.getChild(i) as Element)
            }
        }
        return null
    }
}
