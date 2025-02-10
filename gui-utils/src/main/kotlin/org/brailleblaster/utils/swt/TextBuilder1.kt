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
package org.brailleblaster.utils.swt

import org.brailleblaster.utils.swt.EasySWT.error
import org.eclipse.swt.SWT
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text
import java.util.function.Consumer

/**
 * Provides an easy way to construct a Text box. Notes:
 *
 *
 * <list>
 *  * Like all other ControlBuilders, the setParent() method must be called before build().
 *  * The "enter action" does not need to be set upon creation of the Text. To apply an enter
 * action afterwards, use EasySWT.EasyListeners.keyPress() </list>
 */
class TextBuilder1(override val columns: Int) : ControlBuilder {
    override var parent: Composite? = null
    var width: Int? = null
    var height: Int? = null
    override var swtOptions: Int = SWT.BORDER or SWT.SINGLE
    var onEnter: Consumer<KeyEvent>? = null

    override fun build(): Text {
        if (parent == null) error(EasySWT.NOPARENTSET)
        val newText = Text(parent, swtOptions)
        GridDataBuilder().setHint(width, height).setColumns(columns).applyTo(newText)
        if (onEnter != null) {
            newText.addKeyListener(
                object : KeyAdapter() {
                    override fun keyPressed(e: KeyEvent) {
                        if (e.keyCode == SWT.CR.code || e.keyCode == SWT.KEYPAD_CR) onEnter!!.accept(e)
                    }
                })
        }
        return newText
    }

    override fun setWidth(width: Int): TextBuilder1 {
        this.width = width
        return this
    }

    override fun setHeight(height: Int): TextBuilder1 {
        this.height = height
        return this
    }
}
