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

import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.perspectives.mvc.modules.misc.ClipboardModule
import org.eclipse.swt.SWT

private val localeHandler = LocaleHandler.getDefault()

class CutTool(private val clipboardModule: ClipboardModule) : MenuTool {
    override val topMenu: TopMenu = TopMenu.EDIT
    override val title: String = localeHandler["&Cut"]
    override val accelerator: Int = SWT.MOD1 or 'X'.code
    override val sharedItem: SharedItem = SharedItem.CUT
    override fun onRun(bbData: BBSelectionData) {
        clipboardModule.cut(bbData.manager)
    }
}

class CopyTool(private val clipboardModule: ClipboardModule) : MenuTool {
    override val topMenu: TopMenu = TopMenu.EDIT
    override val title: String = localeHandler["&Copy"]
    override val accelerator: Int = SWT.MOD1 or 'C'.code
    override val sharedItem: SharedItem = SharedItem.COPY
    override fun onRun(bbData: BBSelectionData) {
        clipboardModule.copy(bbData.manager)
    }
}

class CopyUnicodeBrailleTool(private val clipboardModule: ClipboardModule): MenuTool{
    override val topMenu: TopMenu = TopMenu.EDIT
    override val title: String = localeHandler["CopyUnicode"]
    //No accelerator
    override val sharedItem: SharedItem = SharedItem.COPY_UNICODE
    override fun onRun(bbData: BBSelectionData) {
        clipboardModule.copy(bbData.manager, true)
    }
}

class PasteTool(private val clipboardModule: ClipboardModule) : MenuTool {
    override val topMenu: TopMenu = TopMenu.EDIT
    override val title: String = localeHandler["&Paste"]
    override val accelerator: Int = SWT.MOD1 or 'V'.code
    override val sharedItem: SharedItem = SharedItem.PASTE
    override fun onRun(bbData: BBSelectionData) {
        clipboardModule.paste(bbData.manager)
    }
}

class PasteAsMathTool(private val clipboardModule: ClipboardModule) : MenuTool {
    override val topMenu: TopMenu = TopMenu.EDIT
    override val title: String = localeHandler["PasteAsMath"]
    override val accelerator: Int = SWT.MOD1 or SWT.MOD2 or 'M'.code
    override val sharedItem: SharedItem = SharedItem.PASTE_AS_MATH
    override fun onRun(bbData: BBSelectionData) {
        clipboardModule.pasteAsMath(bbData.manager)
    }
}