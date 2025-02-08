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
package org.brailleblaster.printers

import org.brailleblaster.util.ColorManager
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell

class PPStatusBar(documentWindow: Shell?) {
    val statusBar: Label = Label(documentWindow, SWT.NONE)
    fun setColor(color: ColorManager.Colors) {
        statusBar.foreground = ColorManager.getColor(color, statusBar)
    }

    fun setText(text: String?) {
        statusBar.text = text
    }

    init {
        val lbl = GridData(300, 15)
        lbl.horizontalSpan = 2
        statusBar.layoutData = lbl
    }
}