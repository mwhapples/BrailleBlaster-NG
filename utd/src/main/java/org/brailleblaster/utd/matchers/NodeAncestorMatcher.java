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
import nu.xom.Node;
import org.brailleblaster.utd.NamespaceMap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Matches nodes that match the given self* parameters and has a parent, grandparent, or ancestor
 * that matches the given parent* parameters
 */
public class NodeAncestorMatcher implements INodeMatcher {
    private String _selfName;
    private String _selfAttribName;
    private String _selfAttribValue;
    private String _selfAttribNamespace;
    private String _selfNamespace;
    private String _parentName;
    private String _parentAttribName;
    private String _parentAttribValue;
    private String _parentAttribNamespace;
    private String _parentNamespace;
    protected NodeAttributeMatcher selfMatcher;
    protected NodeAttributeMatcher parentMatcher;

    public NodeAncestorMatcher() {
    }

    public NodeAncestorMatcher(String selfName, String selfAttribName, String selfAttribValue, String parentName, String parentAttribName, String parentAttribValue) {
        this._selfName = selfName;
        this._selfAttribName = selfAttribName;
        this._selfAttribValue = selfAttribValue;
        this._parentName = parentName;
        this._parentAttribName = parentAttribName;
        this._parentAttribValue = parentAttribValue;
    }

    @Override
    public boolean isMatch(Node node, @NotNull NamespaceMap namespaces) {
        Document doc = node.getDocument();
        if (selfMatcher != null && selfMatcher.isMatch(node, namespaces)) {
            if (parentMatcher == null) {
                return true;
            }
            Node parent = node.getParent();
            while (parent != null && parent != doc) {
                if (parentMatcher.isMatch(parent, namespaces))
                    return true;
                parent = parent.getParent();
            }
        }
        return false;
    }

    public String getSelfName() {
        return _selfName;
    }

    @XmlAttribute
    public void setSelfName(String selfName) {
        this._selfName = selfName;
        updateSelf();
    }

    public String getSelfAttribName() {
        return _selfAttribName;
    }

    @XmlAttribute
    public void setSelfAttribName(String selfAttribName) {
        this._selfAttribName = selfAttribName;
        updateSelf();
    }

    public String getSelfAttribValue() {
        return _selfAttribValue;
    }

    public String getSelfAttribNamespace() {
        return _selfAttribNamespace;
    }

    public String getSelfNamespace() {
        return _selfNamespace;
    }

    public String getParentName() {
        return _parentName;
    }

    public String getParentAttribName() {
        return _parentAttribName;
    }

    public String getParentAttribValue() {
        return _parentAttribValue;
    }

    public String getParentAttribNamespace() {
        return _parentAttribNamespace;
    }

    public String getParentNamespace() {
        return _parentNamespace;
    }

    public NodeAttributeMatcher getSelfMatcher() {
        return selfMatcher;
    }

    public NodeAttributeMatcher getParentMatcher() {
        return parentMatcher;
    }

    @XmlAttribute
    public void setSelfAttribValue(String selfAttribValue) {
        this._selfAttribValue = selfAttribValue;
        updateSelf();
    }

    @XmlAttribute
    public void setSelfAttribNamespace(String selfAttribNamespace) {
        this._selfAttribNamespace = selfAttribNamespace;
        updateSelf();
    }

    @XmlAttribute
    public void setSelfNamespace(String selfNamespace) {
        this._selfNamespace = selfNamespace;
        updateSelf();
    }

    @XmlAttribute
    public void setParentName(String parentName) {
        this._parentName = parentName;
        updateParent();
    }

    @XmlAttribute
    public void setParentAttribName(String parentAttribName) {
        this._parentAttribName = parentAttribName;
        updateParent();
    }

    @XmlAttribute
    public void setParentAttribValue(String parentAttribValue) {
        this._parentAttribValue = parentAttribValue;
        updateParent();
    }

    @XmlAttribute
    public void setParentAttribNamespace(String parentAttribNamespace) {
        this._parentAttribNamespace = parentAttribNamespace;
        updateParent();
    }

    @XmlAttribute
    public void setParentNamespace(String parentNamespace) {
        this._parentNamespace = parentNamespace;
        updateParent();
    }

    private void updateSelf() {
        selfMatcher = new NodeAttributeMatcher(_selfName, _selfAttribName, _selfAttribValue, _selfAttribNamespace, _selfNamespace);
    }

    private void updateParent() {
        parentMatcher = new NodeAttributeMatcher(_parentName, _parentAttribName, _parentAttribValue, _parentAttribNamespace, _parentNamespace);
    }

    @Override
    public String toString() {
        return "NodeAncestorMatcher{" + "selfMatcher=" + selfMatcher + ", parentMatcher=" + parentMatcher + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + super.hashCode();
        hash = 89 * hash + Objects.hashCode(this.selfMatcher);
        hash = 89 * hash + Objects.hashCode(this.parentMatcher);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        final NodeAncestorMatcher obj = (NodeAncestorMatcher) other;
        if (!super.equals(other))
            return false;
        if (!Objects.equals(this.selfMatcher, obj.selfMatcher))
            return false;
        return Objects.equals(this.parentMatcher, obj.parentMatcher);
    }
}
