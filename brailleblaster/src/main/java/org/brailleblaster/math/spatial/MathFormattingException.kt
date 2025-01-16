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
package org.brailleblaster.math.spatial

import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.util.Notify
import org.brailleblaster.util.Notify.notify

class MathFormattingException : Exception {
    constructor(string: String?) : super(string)
    constructor(string: String?, e: Exception?) : super(string, e)

    companion object {
        private val localeHandler = getDefault()
        private val LINE_TOO_LONG_WARNING = localeHandler["lineTooLongWarning"]
        fun notifyLong() {
            notify(LINE_TOO_LONG_WARNING, Notify.ALERT_SHELL_NAME)
        }

        const val FRACTION_PARSING = "Exception parsing fraction from components"
    }
}