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
package org.brailleblaster.perspectives

import org.brailleblaster.wordprocessor.BBToolBar
import org.brailleblaster.wordprocessor.WPManager

abstract class Perspective {
    abstract var toolBar: BBToolBar
        protected set

    //returns the current perspectives class
    abstract val type: Class<*>
    abstract val controller: Controller
    abstract fun initToolBar(wp: WPManager)
    abstract fun buildToolBar()
    abstract fun rebuildToolBar(wp: WPManager)

    //disposes of menu and any SWT components outside the tab area
    abstract fun dispose()
}