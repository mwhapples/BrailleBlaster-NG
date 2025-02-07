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

import nu.xom.Document
import org.brailleblaster.wordprocessor.BBStatusBar

interface DocumentManager {
    //used to properly dispose of SWT components within the tab area when switching perspectives
    fun dispose()

    //Used when closing a tab and when closing a tab when the program exits.  The controller is responsible for checking and saving a document and performing necessary clean-up
    fun close(): Boolean

    //returns a XOM document, which is passed to another controller when switching perspectives
    val doc: Document

    //Resets the status bar for a perspective dependent message
    fun setStatusBarText(statusBar: BBStatusBar)

    //check before opening a new document whether it should reuse the current tab or open in a new tab
    fun canReuseTab(): Boolean

    //performs any necessary clean-up before opening a document within the current tab
    fun reuseTab(file: String?)
}