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
package org.brailleblaster.utd

import nu.xom.Node
import nu.xom.Text
import kotlin.jvm.Throws

class StyleMap : NodeMatcherMap<IStyle>, IStyleMap {
    constructor() : super(Style())
    constructor(map: NodeMatcherMap<IStyle>) : super(map)

    /**
     * Use default style from the Style definitions
     */
    constructor(defaultValue: IStyle) : super(defaultValue)

    @Throws(NoSuchElementException::class)
    override fun findValue(node: Node): IStyle {
        if (node is Text) {
            return textStyle
        }
        return super.findValue(node)
    }

    companion object {
        private val textStyle: IStyle = Style()
    }
}
