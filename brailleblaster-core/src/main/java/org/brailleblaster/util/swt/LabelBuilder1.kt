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
package org.brailleblaster.util.swt

import org.brailleblaster.util.swt.EasySWT.buildGridData
import org.brailleblaster.util.swt.EasySWT.error
import org.brailleblaster.util.swt.EasySWT.getWidthOfText
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label

/**
 * Provides an easy way to construct a label. Notes:
 *
 *
 * <list>
 *  * Like all other ControlBuilders, the setParent() method must be called before build().
 *  * Although a width can be supplied to the label, when the width is actually applied, it first
 * checks to see if the supplied width is large enough to display the text (taking font size
 * and system settings into consideration), and if not, makes the label just long enough to be
 * displayed </list>
 */
class LabelBuilder1(private val text: String, override val columns: Int) : ControlBuilder {
    override var parent: Composite? = null
    var width: Int? = null
    var height: Int? = null
    override var swtOptions: Int = 0

    override fun build(): Label {
        if (parent == null) error(EasySWT.NOPARENTSET)
        val newLabel = Label(parent, swtOptions)
        newLabel.text = text
        // Make sure the label is wide enough to display the entirety of its text
        val textWidth = getWidthOfText(text)
        width = width?.coerceAtLeast(textWidth) ?: textWidth
        buildGridData().setColumns(columns).setHint(width, height).applyTo(newLabel)
        return newLabel
    }

    override fun setWidth(width: Int): LabelBuilder1 {
        this.width = width
        return this
    }

    override fun setHeight(height: Int): LabelBuilder1 {
        this.height = height
        return this
    }
}
