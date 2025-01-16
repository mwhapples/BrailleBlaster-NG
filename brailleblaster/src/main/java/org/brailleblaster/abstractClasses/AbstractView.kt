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
package org.brailleblaster.abstractClasses

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.utd.properties.UTDElements
import org.eclipse.swt.widgets.Composite

abstract class AbstractView
/** Base constructor for views
 * @param manager : manager for relaying information to models
 * @param parent parent widget
 */(
    protected var manager: Manager, @JvmField
    protected var parent: Composite
) {
    @JvmField
    var hasChanged = false
    var positionFromStart = 0

    /** The offset of the cursor from the starting position of the element
     * used to keep cursors accurate when switching between views.
     */
    @JvmField
    var cursorOffset = 0

    /** Finds the corresponding braille node of a standard text node
     * @param n : node to check
     * @return braille element if markup is correct, null if braille cannot be found
     */
    protected fun getBrlNode(n: Node): Element? {
        val e = n.parent as Element
        val index = e.indexOf(n)
        if (index != e.childCount - 1) {
            if (UTDElements.BRL.isA(e.getChild(index + 1))) return e.getChild(index + 1) as Element
        }
        return null
    }

    /** Sets a lock to pause event listeners
     * @param setting : true to lock, false to unlock
     */
    fun setListenerLock(setting: Boolean) {
        lock = setting
    }

    protected abstract fun setViewData()
    var lock: Boolean
        get() = Companion.lock
        protected set(value) {
            Companion.lock = value
        }
    var uiLock: Boolean
        get() = Companion.uiLock
        set(value) {
            Companion.uiLock = value
        }

    companion object {
        /**
         * State of listener lock
         */
        private var lock = false
        private var uiLock = false
    }
}
