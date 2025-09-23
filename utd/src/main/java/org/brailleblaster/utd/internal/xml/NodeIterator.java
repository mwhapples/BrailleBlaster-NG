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
package org.brailleblaster.utd.internal.xml;

import nu.xom.Node;
import nu.xom.ParentNode;

import java.util.Iterator;

public class NodeIterator implements Iterator<Node> {
    private final Node startNode;
    private final boolean stayInsideStartNode;
    private final boolean forward;
    private Node nextNode;

    public NodeIterator(Node startNode, boolean stayInsideStartNode, boolean forward) {
        this.startNode = startNode;
        this.nextNode = startNode;
        this.stayInsideStartNode = stayInsideStartNode;
        this.forward = forward;
    }

    @Override
    public boolean hasNext() {
        return nextNode != null;
    }

    @Override
    public Node next() {
        Node curNode = nextNode;
        doAdvance();
        return curNode;
    }

    public void doAdvance() {
        if (nextNode.getChildCount() != 0) {
            nextNode = nextNode.getChild(forward ? 0 : nextNode.getChildCount() - 1);
        } else {
            nextNode = itrNextNode(
                    nextNode,
                    stayInsideStartNode ? startNode : null,
                    forward
            );
        }
    }

    /**
     * Safe following node impl that stops once outside of the given start node
     *
     * @param stopNode     Parent we are not escaping from
     * @param inputCurNode Assumed to be some (maybe nested) child of startNode
     */
    public static Node itrNextNode(Node inputCurNode, Node stopNode, boolean forward) {
        Node curNode = inputCurNode;
        //TODO: This will break if inputCurNode is not descendant from stopNode
        while (stopNode == null || stopNode != curNode) {
            ParentNode parent = curNode.getParent();
            if (parent == null) {
                //No more nodes available
                return null;
            }
            int index = parent.indexOf(curNode);
            if (forward && index != parent.getChildCount() - 1) {
                return parent.getChild(index + 1);
            } else if (!forward && index > 0) {
                return parent.getChild(index - 1);
            }
            //Last entry in parent, get parents sibling
            curNode = parent;
        }
        //Finished getting all childrens parents
        return null;
    }
}
