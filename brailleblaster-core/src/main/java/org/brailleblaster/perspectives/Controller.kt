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

import org.brailleblaster.archiver2.Archiver2
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.util.Notify
import org.brailleblaster.util.Notify.notify
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import java.io.File

abstract class Controller(val wpManager: WPManager) : DocumentManager {
    var tab: CTabItem? = null
        protected set
    abstract val archiver: Archiver2

    /**
     * Pretty name of file, either filename or Untitled variant
     * @return
     */
    var documentName: String? = null
        private set

    fun setTabTitle(pathName: String?) {
        documentName = if (archiver.path == Manager.DEFAULT_FILE) {
            "New Document"
        } else if (pathName != null) {
            val index = pathName.lastIndexOf(File.separatorChar)
            if (index == -1) pathName else pathName.substring(index + 1)
        } else {
            if (docCount == 1) "Untitled" else "Untitled #$docCount"
        }
        tab!!.text = documentName
    }

    /** Creates a Notify class alert box if debugging is not active
     * @param notify : String to be used in an alert box, should already be localized
     */
    fun notify(notify: String?) {
        notify(notify, Notify.EXCEPTION_SHELL_NAME)
    }

    protected fun addTabItem(folder: CTabFolder?): CTabItem {
        return CTabItem(folder, SWT.CLOSE).also {
            tab = it
        }
    }

    companion object {
        @JvmField
		protected var docCount = 0
    }
}
