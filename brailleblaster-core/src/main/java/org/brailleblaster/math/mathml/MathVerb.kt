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
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.math.ascii.ASCII2MathML.translate

fun interface MathVerb {
    fun transform(s: MathSubject): Node
    enum class Verb {
        MakeMath, WrapInText
    }

    companion object {
        fun newText(s: MathSubject): Node {
            return Text(s.string)
        }

        private fun wrapInMath(s: MathSubject): Node {
            val mathml = BBX.INLINE.MATHML.create()
            mathml.appendChild(translate(s.string))
            return mathml
        }

        fun sameAction(current: MathAction, next: MathAction): Boolean {
            return current.verbName.toString() == next.verbName.toString()
        }

        val makeMath: MathVerb = MathVerb { s: MathSubject -> wrapInMath(s) }
        val wrapInText: MathVerb = MathVerb { s: MathSubject -> newText(s) }
    }
}