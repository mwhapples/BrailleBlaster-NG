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
package org.brailleblaster.perspectives.braille.mapping.elements

import nu.xom.Node
import org.brailleblaster.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.ReadOnlyTextMapElement.Companion.getMessage
import org.brailleblaster.perspectives.braille.mapping.interfaces.Uneditable
import org.brailleblaster.util.Notify
import org.brailleblaster.util.Notify.notify

class ReadOnlyTabTextMapElement(n: Node) : TabTextMapElement(n), Uneditable {
    var invalidMessage: String = localeHandler["readOnlyWarning"]

    init {
        val individualMessage = getMessage(n)
        if (individualMessage != null) {
            invalidMessage = individualMessage
        }
    }

    override fun blockEdit(m: Manager) {
        notify(invalidMessage, Notify.ALERT_SHELL_NAME)
    }

    companion object {
        private val localeHandler = getDefault()
    }
}
