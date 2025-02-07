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
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.eclipse.swt.SWT
import java.util.function.Consumer

sealed class ToggleViewTool(override val title: String, override val accelerator: Int, val onSelect: Consumer<BBSelectionData>, override val sharedItem: SharedItem?) : CheckMenuTool {
    class TogglePrintViewTool(override val active: Boolean, onSelect: Consumer<BBSelectionData>) : ToggleViewTool("Print", SWT.MOD3 + 'P'.code, onSelect, SharedItem.PRINT_VIEW)
    class ToggleBrailleViewTool(override val active: Boolean, onSelect: Consumer<BBSelectionData>) : ToggleViewTool("Braille", 0, onSelect, SharedItem.BRAILLE_VIEW)
    class ToggleStyleViewTool(override val active: Boolean, onSelect: Consumer<BBSelectionData>) : ToggleViewTool("Style", 0, onSelect, SharedItem.STYLE_VIEW)
    class ToggleBreadCrumbsToolbarTool(override val active: Boolean, onSelect: Consumer<BBSelectionData>) : ToggleViewTool("Breadcrumbs", 0, onSelect, null)

    override val topMenu: TopMenu = TopMenu.VIEW
    override fun onRun(bbData: BBSelectionData) = onSelect.accept(bbData)
}