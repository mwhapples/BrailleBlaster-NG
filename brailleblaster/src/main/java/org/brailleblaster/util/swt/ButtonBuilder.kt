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

import org.brailleblaster.util.swt.EasyListeners.selection
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import java.util.function.Consumer

class ButtonBuilder(parent: Composite, style: Int) :
    AbstractSWTBuilder<Button, ButtonBuilder>(Button(parent, style)) {
    fun text(text: String?): ButtonBuilder {
        widget.text = text
        return this
    }

    fun onSelection(onSelection: Consumer<SelectionEvent>): ButtonBuilder {
        selection(widget, onSelection)
        return this
    }

    fun selected(isSelected: Boolean): ButtonBuilder {
        widget.selection = isSelected
        return this
    }
}
