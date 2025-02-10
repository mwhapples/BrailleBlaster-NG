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
package org.brailleblaster.math.mathml

import nu.xom.Node
import org.brailleblaster.math.ascii.ASCII2MathML.translate

fun interface GetMathNode {
    fun getNode(o: Any): Node

    companion object {
        @JvmField
		val fromNode: GetMathNode = GetMathNode { n -> n as Node }
        @JvmField
		val fromText: GetMathNode = GetMathNode { text: Any -> translate((text as String)) }
    }
}