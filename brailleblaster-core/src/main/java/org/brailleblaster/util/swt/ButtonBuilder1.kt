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

import org.brailleblaster.util.swt.EasySWT.error
import org.brailleblaster.util.swt.EasySWT.getWidthOfText
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import java.util.function.Consumer

/**
 * Provides an easy way to construct a Button. Notes:
 *
 *
 * <list>
 *  * Like all other ControlBuilders, the setParent() method must be called before build(). In
 * ButtonBuilder's case, setText() must also be called.
 *  * Although a width can be supplied to the button, when the width is actually applied, it
 * first checks to see if the supplied width is large enough to display the button(taking font
 * size and system settings into consideration), and if not, makes the button long enough to
 * be displayed and also leaves a small amount of "padding". </list>
 */
class ButtonBuilder1 : ControlBuilder {
    override var parent: Composite? = null
    @JvmField
    var text: String? = null
    var onClick: Consumer<SelectionEvent>? = null
    override var columns: Int = 1
    var width: Int? = null
    var height: Int? = null
    override var swtOptions: Int = SWT.PUSH

    constructor(parent: Composite?) {
        this.parent = parent
    }

    constructor(text: String?, columns: Int, onClick: Consumer<SelectionEvent>?) {
        this.text = text
        this.columns = columns
        this.onClick = onClick
    }

    override fun build(): Button {
        if (parent == null) error(EasySWT.NOPARENTSET)
        if (text == null) error(EasySWT.NOTEXTSET)
        val newButton = Button(parent, swtOptions)
        newButton.text = text
        if (onClick != null) {
            newButton.addSelectionListener(
                object : SelectionAdapter() {
                    override fun widgetSelected(e: SelectionEvent) {
                        onClick!!.accept(e)
                    }
                })
        }
        // Make sure the width does not cause the text of the button to be cut off
        val textWidth = getWidthOfText(text) + EasySWT.TEXT_PADDING
        width = width?.coerceAtLeast(textWidth) ?: textWidth
        GridDataBuilder().setHint(width, height).setColumns(columns).applyTo(newButton)
        return newButton
    }

    override fun setWidth(width: Int): ButtonBuilder1 {
        this.width = width
        return this
    }

    override fun setHeight(height: Int): ButtonBuilder1 {
        this.height = height
        return this
    }
}
