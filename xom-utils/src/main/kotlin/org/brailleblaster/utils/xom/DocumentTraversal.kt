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
package org.brailleblaster.utils.xom

import com.google.common.base.Preconditions
import nu.xom.Comment
import nu.xom.DocType
import nu.xom.Document
import nu.xom.Element
import nu.xom.NoSuchChildException
import nu.xom.Node
import nu.xom.ProcessingInstruction
import nu.xom.Text
import java.util.Deque
import java.util.LinkedList

/**
 * Traverse a XML document
 *
 *
 * This will traverse an XML document whilst calling callback methods upon certain events. There
 * may be similarities to SAX, however the event callbacks still have access to the XOM document
 * model which may reduce the need to hold internal state in the parser.
 *
 *
 * Default implementations of the callbacks are provided, however these are minimal
 * implementations and simply ignore the event.
 */
abstract class DocumentTraversal {
    private class PathElement(val element: Element) {
        var curIndex: Int = 0
            set(value) {
                field = Preconditions.checkPositionIndex(value, element.childCount)
            }

        val curChild: Node
            get() {
                if (!hasChildren()) {
                    throw NoSuchChildException("Element is empty")
                }
                return element.getChild(curIndex)
            }

        fun hasChildren(): Boolean {
            return element.childCount > 0
        }
    }

    fun traverseDocument(doc: Document) {
        Preconditions.checkNotNull(doc.rootElement)
        onStartDocument(doc)
        doc.docType?.let { onDocType(it) }
        val stack: Deque<PathElement> = LinkedList()
        var curPath: PathElement? = PathElement(doc.rootElement)
        while (curPath != null) {
            val curElement = curPath.element
            var descend = true
            // Have we just entered the element.
            if (curPath.curIndex == 0) {
                descend = onStartElement(curElement)
                stack.push(curPath)
            }
            // Are we at the end of an element.
            // Alternatively did the handler request not to descend.
            if (curPath.curIndex >= curElement.childCount || !descend) {
                onEndElement(curElement)
                stack.pop()
                curPath = stack.peek()
                // Move to the next child of curPath for the next loop iteration.
                curPath.curIndex += 1
                continue
            }
            val curNode = curPath.curChild
            if (curNode is Element) {
                curPath = PathElement(curNode)
                continue
            } else if (curNode is Text) {
                onText(curNode)
            } else if (curNode is ProcessingInstruction) {
                onProcessingInstruction(curNode)
            } else if (curNode is Comment) {
                onComment(curNode)
            } else {
                // Unknown node type
                onUnknownNode(curNode)
            }
            curPath.curIndex += 1
        }
        onEndDocument(doc)
    }

    /**
     * Notification of start of an element.
     *
     *
     * When walking the document, when the start of an element is encountered this method will be
     * called. The handler can indicate whether to descend into the element through the return value.
     *
     * @param e The element which is starting.
     * @return True if to descend into the element, false if not.
     */
    protected open fun onStartElement(e: Element): Boolean {
        return true
    }

    /**
     * Notification of the end of an element.
     *
     *
     * This method is called when the end of a element is encountered.
     *
     * @param e The element which is ending.
     */
    protected open fun onEndElement(e: Element) {}

    /**
     * Notification of the start of a document.
     *
     * @param d The document being started.
     */
    protected open fun onStartDocument(d: Document) {}

    /**
     * Notification of the end of a document.
     *
     * @param d The document being ended.
     */
    protected open fun onEndDocument(d: Document) {}

    /**
     * Notification of a text node.
     *
     * @param t The text node which is being encountered.
     */
    protected open fun onText(t: Text) {}

    /**
     * Notifyication of DOCTYPE
     *
     * @param dt The doctype node.
     */
    protected open fun onDocType(dt: DocType) {}

    /**
     * Notification of a processing instruction being encountered.
     *
     * @param pi The process instruction being encountered.
     */
    protected open fun onProcessingInstruction(pi: ProcessingInstruction) {}

    /**
     * Notification of a comment being encountered.
     *
     * @param c The comment being encountered.
     */
    protected open fun onComment(c: Comment) {}

    /**
     * Notification of unknown node type.
     *
     *
     * This method is only called should the node not be of a known type handled by one of the
     * other more specific handler methods.
     *
     * @param n The node.
     */
    protected open fun onUnknownNode(n: Node) {}
}