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
package org.brailleblaster.perspectives.braille.toolbar

import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem

class ToolBarNewLine(val saveInSettings: Boolean) : IToolBarElement {

    override fun createSection(parent: ToolBar, sharedToolBars: MutableMap<SharedItem, ToolItem>): ToolBar {
        return ToolBar(parent.parent, SWT.HORIZONTAL)
    }
}
