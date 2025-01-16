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

import org.brailleblaster.perspectives.braille.toolbar.ToolBarSettings.userSettings
import org.brailleblaster.wordprocessor.BBToolBar
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.widgets.Shell

class BrailleToolBar(shell: Shell, wp: WPManager) : BBToolBar {
    private val tbb: ToolBarBuilder

    init {
        val toolBarSettings: List<ToolBarSettings.Settings> = userSettings
        tbb = ToolBarBuilder(
            shell,
            { wp.buildToolBar() },
            { wp.onToolBarExpand() },
            { wp.onToolBarCondense() })
        for (setting in toolBarSettings) {
            tbb.createSection(setting)
        }
    }

    fun build() {
        tbb.build()
    }

    override fun dispose() {
        tbb.dispose()
    }

    override val height: Int
        get() = tbb.height

    fun addToToolBar(custTB: CustomToolBarBuilder) {
        tbb.createCustomToolBar(custTB)
    }
}
