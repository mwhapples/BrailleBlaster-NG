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
package org.brailleblaster.utd.matchers;

import jakarta.xml.bind.annotation.XmlAttribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;
import org.apache.commons.lang3.StringUtils;
import org.brailleblaster.utd.NamespaceMap;
import org.jetbrains.annotations.NotNull;

/**
 * This class is meant to match a list unless is follows a cell 5 or cell 7
 * heading, top box line, directions, or is the child of another list.
 */
public class MultipleExclusionsMatcher extends NodeNameMatcher {

    private String nodeName;
    private String sometimesParent;
    private String repeatOnce;
    private String[] neverParentParts;

    public MultipleExclusionsMatcher() {
        super();
    }

    @Override
    public boolean isMatch(@NotNull Node node, @NotNull NamespaceMap namespaces) {

        if (node instanceof Element currentElement) {
            if (!currentElement.getLocalName().equals(nodeName)) {
                return false;
            } else {
                // The list cannot have an ancestor that is a list
                NodeAncestorMatcher ancestorMatcher = new NodeAncestorMatcher();
                ancestorMatcher.setSelfName(nodeName);
                ancestorMatcher.setParentName(nodeName);
                if (ancestorMatcher.isMatch(node, namespaces))
                    return false;
            }
        } else {
            // Not an element, don't format as list
            return false;
        }
        Document doc = node.getDocument();
        Node currentParent = node.getParent();
        if (currentParent != doc) {
            Element parentElement = (Element) currentParent;

            if (currentParent != doc && parentElement.getLocalName().equals(repeatOnce)) {
                // Ancestor cannot be side bar unless that side bar has
                // a side bar parent.
                NodeAncestorMatcher ancestorMatcher = new NodeAncestorMatcher();
                ancestorMatcher.setSelfName(repeatOnce);
                ancestorMatcher.setParentName(repeatOnce);
                if (ancestorMatcher.isMatch(currentParent, namespaces))
                    return true;
                else {
                    for (int i = 0; i < currentParent.getChildCount(); i++) {
                        if (currentParent.getChild(i) != node && currentParent.getChild(i) instanceof Text) {
                            return true;
                        }
                    }
                }
            }
            Node currentGrandparent = currentParent.getParent();
            if (currentGrandparent instanceof Element grandparentElement) {

                while (currentParent != null && currentGrandparent != null
                        && currentParent != doc) {
                    for (String neverParentPart : neverParentParts) {
                        // Parent can never be h2|h3|h4|h5|h6
                        if (parentElement.getLocalName().equals(
                                neverParentPart)) {
                            return false;
                        }
                        // Parent can be p unless its parent is
                        // h2|h3|h4|h5|h6
                        if (parentElement.getLocalName().equals(
                                sometimesParent)
                                && grandparentElement.getLocalName()
                                .equals(neverParentPart)) {
                            return false;
                        }
                    }
                    currentParent = currentParent.getParent();
                    if (currentParent instanceof Element)
                        parentElement = (Element) currentParent;
                    currentGrandparent = currentGrandparent.getParent();
                    if (currentGrandparent instanceof Element)
                        grandparentElement = (Element) currentGrandparent;

                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @XmlAttribute
    @Override
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @XmlAttribute
    public void setNeverParent(String neverParent) {
        neverParentParts = StringUtils.split(neverParent, "|");
    }

    @XmlAttribute
    public void setSometimesParent(String sometimesParent) {
        this.sometimesParent = sometimesParent;
    }

    @XmlAttribute
    public void setRepeatOnce(String repeatOnce) {
        this.repeatOnce = repeatOnce;
    }

}
