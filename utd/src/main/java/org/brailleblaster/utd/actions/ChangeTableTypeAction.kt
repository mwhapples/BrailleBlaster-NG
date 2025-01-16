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
package org.brailleblaster.utd.actions

import nu.xom.Node
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.properties.BrailleTableType
import java.util.*
import jakarta.xml.bind.annotation.XmlAttribute

open class ChangeTableTypeAction : GenericBlockAction {
    @XmlAttribute
    var table: BrailleTableType? = null
        private set

    @Suppress("UNUSED")
    private constructor() {
        // Needed for JAXB
    }

    constructor(table: BrailleTableType?) {
        this.table = table
    }

    override fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan> {
        return translate(node, table!!, context)
    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 67 * hash + Objects.hashCode(table)
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val o = other as ChangeTableTypeAction
        return table === o.table
    }
}