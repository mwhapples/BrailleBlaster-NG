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
package org.brailleblaster.utd.internal

import jakarta.xml.bind.annotation.*
import org.brailleblaster.utd.NamespaceMap
import org.brailleblaster.utd.actions.IAction
import org.brailleblaster.utd.actions.SkipAction
import org.brailleblaster.utd.matchers.AlwaysMatcher
import org.brailleblaster.utd.matchers.INodeMatcher

/**
 * Represent a SemanticActionMap in list form for persisting to XML.
 */
@XmlType(propOrder = ["namespaces", "semanticEntries"])
@XmlRootElement(name = "actionMap")
class AdaptedActionMap @JvmOverloads constructor(@get:XmlElement(name = "entry") var semanticEntries: MutableList<Entry> = ArrayList()) {
    @XmlRootElement(name = "entry")
    @XmlAccessorType(XmlAccessType.FIELD)
    class Entry(@field:XmlElement(name = "matcher") val matcher: INodeMatcher, @field:XmlElement(name = "action") val action: IAction) {

        // JAXB requires the following no args constructor
        @Suppress("UNUSED")
        private constructor() : this(AlwaysMatcher, SkipAction())
    }

    var namespaces: NamespaceMap = NamespaceMap()

}