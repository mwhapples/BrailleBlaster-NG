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
	private String selfName;
	private String selfAttribName;
	private String selfAttribValue;
	private String selfAttribNamespace;
	private String selfNamespace;
	private String parentName;
	private String parentAttribName;
	private String parentAttribValue;
	private String parentAttribNamespace;
	private String parentNamespace;
	protected NodeAttributeMatcher selfMatcher;
	protected NodeAttributeMatcher parentMatcher;

	public NodeAncestorMatcher() {
	}

	public NodeAncestorMatcher(String selfName, String selfAttribName, String selfAttribValue, String parentName, String parentAttribName, String parentAttribValue) {
		this.selfName = selfName;
		this.selfAttribName = selfAttribName;
		this.selfAttribValue = selfAttribValue;
		this.parentName = parentName;
		this.parentAttribName = parentAttribName;
		this.parentAttribValue = parentAttribValue;
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

	@XmlAttribute
	public void setSelfName(String selfName) {
		this.selfName = selfName;
		updateSelf();
	}

	@XmlAttribute
	public void setSelfAttribName(String selfAttribName) {
		this.selfAttribName = selfAttribName;
		updateSelf();
	}

	@XmlAttribute
	public void setSelfAttribValue(String selfAttribValue) {
		this.selfAttribValue = selfAttribValue;
		updateSelf();
	}

	@XmlAttribute
	public void setSelfAttribNamespace(String selfAttribNamespace) {
		this.selfAttribNamespace = selfAttribNamespace;
		updateSelf();
	}

	@XmlAttribute
	public void setSelfNamespace(String selfNamespace) {
		this.selfNamespace = selfNamespace;
		updateSelf();
	}

	@XmlAttribute
	public void setParentName(String parentName) {
		this.parentName = parentName;
		updateParent();
	}

	@XmlAttribute
	public void setParentAttribName(String parentAttribName) {
		this.parentAttribName = parentAttribName;
		updateParent();
	}

	@XmlAttribute
	public void setParentAttribValue(String parentAttribValue) {
		this.parentAttribValue = parentAttribValue;
		updateParent();
	}

	@XmlAttribute
	public void setParentAttribNamespace(String parentAttribNamespace) {
		this.parentAttribNamespace = parentAttribNamespace;
		updateParent();
	}

	@XmlAttribute
	public void setParentNamespace(String parentNamespace) {
		this.parentNamespace = parentNamespace;
		updateParent();
	}

	private void updateSelf() {
		selfMatcher = new NodeAttributeMatcher(selfName, selfAttribName, selfAttribValue, selfAttribNamespace, selfNamespace);
	}

	private void updateParent() {
		parentMatcher = new NodeAttributeMatcher(parentName, parentAttribName, parentAttribValue, parentAttribNamespace, parentNamespace);
	}

	public String getSelfName() {
		return selfName;
	}

	public String getSelfAttribName() {
		return selfAttribName;
	}

	public String getSelfAttribValue() {
		return selfAttribValue;
	}

	public String getSelfAttribNamespace() {
		return selfAttribNamespace;
	}

	public String getSelfNamespace() {
		return selfNamespace;
	}

	public String getParentName() {
		return parentName;
	}

	public String getParentAttribName() {
		return parentAttribName;
	}

	public String getParentAttribValue() {
		return parentAttribValue;
	}

	public String getParentAttribNamespace() {
		return parentAttribNamespace;
	}

	public String getParentNamespace() {
		return parentNamespace;
	}

	public NodeAttributeMatcher getSelfMatcher() {
		return selfMatcher;
	}

	public NodeAttributeMatcher getParentMatcher() {
		return parentMatcher;
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
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NodeAncestorMatcher other = (NodeAncestorMatcher) obj;
		if (!super.equals(obj))
			return false;
		if (!Objects.equals(this.selfMatcher, other.selfMatcher))
			return false;
        return Objects.equals(this.parentMatcher, other.parentMatcher);
    }
}
