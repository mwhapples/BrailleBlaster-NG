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
import nu.xom.ParentNode
import org.brailleblaster.math.mathml.MathVerb.Verb
import org.brailleblaster.util.Utils

class MathAction(var subject: MathSubject, var verbName: Verb) {
    var verb: MathVerb = if (verbName == Verb.MakeMath) MathVerb.makeMath else MathVerb.wrapInText

    fun execute(parent: Node?, index: Int) {
        val n = verb.transform(subject)
        if (parent != null) {
            Utils.insertChildCountSafe(parent as ParentNode, n, index)
        }
    }

    companion object {
        fun execute(actionList: MutableList<MathAction>, parent: Node?, index: Int) {
            var i = index
            if (actionList.isEmpty()) {
                return
            }
            for (a in combineAdjacent(actionList)) {
                val n = a.verb.transform(a.subject)
                if (parent != null) {
                    Utils.insertChildCountSafe(parent as ParentNode, n, i++)
                }
            }
        }

        private fun combineAdjacent(array: MutableList<MathAction>): MutableList<MathAction> {
            if (array.size <= 1) {
                return array
            }
            val newArray = mutableListOf<MathAction>()
            var current: MathAction? = array[0]
            var next: MathAction? = array[1]
            while (current != null) {
                if (next != null && MathVerb.sameAction(current, next)) {
                    current.subject = combineSubject(current.subject, next.subject)
                    array.remove(next)
                    next = if (array.size > 1) array[1] else null
                } else {
                    newArray.add(current)
                    array.remove(current)
                    current = next
                    next = if (array.size > 1) array[1] else null
                }
            }
            return newArray
        }

        private fun combineSubject(s1: MathSubject, s2: MathSubject): MathSubject {
            val s = s1.string + s2.string
            return MathSubject(s)
        }
    }
}