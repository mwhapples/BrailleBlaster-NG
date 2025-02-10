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
package org.brailleblaster.perspectives.braille.mapping.interfaces

import org.brailleblaster.perspectives.braille.Manager

/**
 * TextMapElements that implement Uneditable will block any kind
 * of editing when the cursor is inside this TME.
 */
interface Uneditable {
    /**
     * blockEdit is called when a user attempts to make an edit
     * inside this TME. This is where a message would be displayed
     * if necessary (e.g. a warning dialog)
     */
    fun blockEdit(m: Manager) {
        //Do nothing
    }
}
