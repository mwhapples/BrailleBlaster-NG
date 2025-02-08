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
package org.brailleblaster.tools

import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.search.SearchConstants
import org.eclipse.swt.SWT

object RepeatSearchTool : MenuTool {
    override val topMenu = TopMenu.EDIT
    override val title = SearchConstants.REPEAT_SEARCH
    override val accelerator = SWT.F3
    override val enableListener = null
    override val sharedItem = null
    override fun onRun(bbData: BBSelectionData) {
        bbData.manager.repeatLastSearch()
    }
}