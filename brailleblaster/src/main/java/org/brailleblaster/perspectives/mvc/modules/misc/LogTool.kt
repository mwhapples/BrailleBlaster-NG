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
package org.brailleblaster.perspectives.mvc.modules.misc

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.tools.MenuToolListener
import org.brailleblaster.wordprocessor.LogViewerDialog

object LogTool : MenuToolListener {
    private val localeHandler = getDefault()
    override val topMenu = TopMenu.HELP
    override val title = localeHandler["View&Log"]
    override fun onRun(bbData: BBSelectionData) {
        val parent = bbData.wpManager.shell
        val logDialog = LogViewerDialog(parent)
        logDialog.text = "Log Viewer"
        logDialog.open()
    }
}