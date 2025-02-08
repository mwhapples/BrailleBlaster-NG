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

import nu.xom.ParentNode
import org.brailleblaster.perspectives.braille.Manager

/**
 * TextMapElements that implement Deletable will delete a node (not text)
 * when backspace or delete is pressed.
 */
interface Deletable {
    /**
     * deleteNode is called when backspace or delete is pressed on this
     * TextMapElement and handles the logic behind detaching the element
     * from the DOM. The returned node will be passed into the reformatter
     * unless null is returned. If null is returned, the deleteNode method
     * is expected to handle its own reformatting.
     */
    fun deleteNode(m: Manager): ParentNode?
}
