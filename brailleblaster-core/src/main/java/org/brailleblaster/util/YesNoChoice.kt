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
package org.brailleblaster.util

import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.MessageBox

class YesNoChoice(message: String?, includeCancel: Boolean) {
    /** Show the user a message and give her a yes/no choice.  */
    var result: Int = MessageBox(
        WPManager.getInstance().shell,
        if (includeCancel) SWT.YES or SWT.NO or SWT.CANCEL else SWT.YES or SWT.NO
    ).apply {
        this.message = message
    }.open()

    companion object {
        /**
         * @param message Message to display
         * @return True if user clicked yes
         */
        @JvmStatic
        fun ask(message: String?): Boolean {
            return YesNoChoice(message, false).result == SWT.YES
        }
    }
}