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

import nu.xom.Node
import nu.xom.Text

class TextSplitter(splitAt: Int, val node: Node) {
    @JvmField
	val first: Text? = if (splitAt == 0) null else Text(node.value.substring(0, splitAt))

    @JvmField
	val last: Text = Text(node.value.substring(splitAt))

}