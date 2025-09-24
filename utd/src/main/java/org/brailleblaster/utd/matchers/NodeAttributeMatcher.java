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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import org.apache.commons.lang3.StringUtils;
import org.brailleblaster.utd.NamespaceMap;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class NodeAttributeMatcher extends NodeNameMatcher {
    @XmlAttribute
    public final String selfAttribName;
    private String[] _selfAttribValue;
    @XmlAttribute
    public final String selfAttribNamespace;

    public NodeAttributeMatcher() {
        super();
        selfAttribName = null;
        _selfAttribValue = null;
        selfAttribNamespace = null; //Default per element.getAttribute(String)
    }

    public NodeAttributeMatcher(String selfName, String selfAttribName, String selfAttribValue) {
        this(selfName, selfAttribName, selfAttribValue, null, null);
    }

    public NodeAttributeMatcher(String selfName, String selfAttribName, String selfAttribValue, String selfAttribNamespace, String namespace) {
        super(selfName, namespace);
        this.selfAttribName = selfAttribName;
        setSelfAttribValue(selfAttribValue);
        this.selfAttribNamespace = selfAttribNamespace;
    }

    @Override
    public boolean isMatch(@NotNull Node node, @NotNull NamespaceMap namespaces) {
        if (!(node instanceof Element nodeElement)) {
            return false;
        }

        if (super.getNodeName() != null && !super.isMatch(node, namespaces))
            return false;

        if (selfAttribName == null) {
            //No attribute given to check
            return true;
        }

        //Check attributes

        String namespace;
        if (selfAttribNamespace != null) {
            namespace = namespaces.getNamespace(selfAttribNamespace);
            if (namespace == null) {
                throw new NullPointerException("Searching for namespace " + selfAttribNamespace + " but does not exist in namespace map");
            }
        } else {
            namespace = "";
        }

        Attribute attrib = nodeElement.getAttribute(selfAttribName, namespace);
        if (attrib == null)
            return false;

        if (_selfAttribValue == null) {
            //Key exists but not value to check
            return true;
        }

        for (String curValue : _selfAttribValue) {
            if (curValue.equals(attrib.getValue())) {
                return true;
            }
        }
        return false;
    }

    @XmlAttribute
    public String getSelfAttribValue() {
        return String.join("|", _selfAttribValue);
    }


    public void setSelfAttribValue(String value) {
        _selfAttribValue = StringUtils.split(value, '|');
    }

    public List<String> getSelfAttribValueList() {
        return Arrays.asList(_selfAttribValue);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + super.hashCode();
        hash = 83 * hash + Objects.hashCode(this.selfAttribName);
        //Convert to string as different arrays are never equal
        hash = 83 * hash + Objects.hashCode(getSelfAttribValue());
        hash = 83 * hash + Objects.hashCode(this.selfAttribNamespace);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        final NodeAttributeMatcher obj = (NodeAttributeMatcher) other;
        if (!super.equals(other))
            return false;
        if (!Objects.equals(this.selfAttribName, obj.selfAttribName))
            return false;
        //Convert to string as different arrays are never equal
        if (!Objects.equals(this.getSelfAttribValue(), obj.getSelfAttribValue()))
            return false;
        return Objects.equals(this.selfAttribNamespace, obj.selfAttribNamespace);
    }

    @Override
    public @NotNull String toString() {
        return "NodeAttributeMatcher{" + "selfAttribName=" + selfAttribName + ", selfAttribValue=" + getSelfAttribValue() + ", selfAttribNamespace=" + selfAttribNamespace + ", super=" + super.toString() + '}';
    }
}
