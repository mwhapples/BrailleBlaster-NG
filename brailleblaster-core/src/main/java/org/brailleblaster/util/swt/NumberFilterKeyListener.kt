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

import org.eclipse.swt.SWT
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.widgets.Text

class NumberFilterKeyListener(private val noDecimal: Boolean) : KeyAdapter() {
    override fun keyPressed(e: KeyEvent) {
        val t = e.widget as Text
        if (!Character.isDigit(e.character) && e.keyCode != SWT.BS.code && e.keyCode != SWT.DEL.code && e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT && e.keyCode != SWT.HOME && e.keyCode != SWT.END) {
            if (noDecimal && e.character == '.') e.doit = false
            else if (e.character == '.' && t.selectionText.contains(".")) e.doit = true
            else if (e.character != '.' || t.text.contains(".")) e.doit = false
        }
    }
}
