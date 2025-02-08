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
package org.brailleblaster.perspectives.braille.views.tree

import nu.xom.Element
import org.brailleblaster.abstractClasses.BBView
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

interface BBTree : BBView {
    fun setRoot(e: Element)
    val root: TreeItem?
    fun setSelection(t: TextMapElement)
    fun getSelection(t: TextMapElement?): TextMapElement?
    val selectionIndex: Int
    fun clearTree()
    val tree: Tree
    val itemPath: ArrayList<Int>
    fun dispose()
}
