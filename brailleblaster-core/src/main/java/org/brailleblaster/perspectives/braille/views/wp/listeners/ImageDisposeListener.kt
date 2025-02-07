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
package org.brailleblaster.perspectives.braille.views.wp.listeners

import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener

/**
 * Handles disposal of images embedded in styled text widget when widget is disposed.
 * Require in styled text views if images are embedded.  Set SWT.Dispose
 * as event when setting listener on widget
 */
class ImageDisposeListener(val view: StyledText) : Listener {
    override fun handleEvent(e: Event) {
        val styles = view.styleRanges
        for (style in styles) {
            (style.data as? Image?)?.dispose()
        }
    }
}
