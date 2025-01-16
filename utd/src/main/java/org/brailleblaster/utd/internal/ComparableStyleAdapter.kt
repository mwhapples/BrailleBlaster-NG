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

import org.brailleblaster.utd.ComparableStyle
import org.brailleblaster.utd.config.StyleDefinitions
import java.util.*
import jakarta.xml.bind.annotation.adapters.XmlAdapter

class ComparableStyleAdapter : XmlAdapter<String?, ComparableStyle?> {
    private var styleDefs: StyleDefinitions

    @Suppress("unused")
    constructor() {
        styleDefs = StyleDefinitions()
    }

    constructor(styleDefs: StyleDefinitions?) {
        this.styleDefs = Objects.requireNonNullElseGet(styleDefs) { StyleDefinitions() }
    }

    override fun marshal(v: ComparableStyle?): String? {
        return v?.styleName
    }

    override fun unmarshal(v: String?): ComparableStyle? {
        return v?.let { ComparableStyle(styleDefs, it) }
    }
}