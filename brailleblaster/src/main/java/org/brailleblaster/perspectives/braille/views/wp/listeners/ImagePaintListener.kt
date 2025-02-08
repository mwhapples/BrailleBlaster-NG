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

import org.eclipse.swt.custom.PaintObjectEvent
import org.eclipse.swt.custom.PaintObjectListener
import org.eclipse.swt.graphics.Image

/**
 * redraws images on paint event, required if
 * images are embedded in styled text widgets
 */
class ImagePaintListener : PaintObjectListener {
    override fun paintObject(event: PaintObjectEvent) {
        val style = event.style
        val image = style.data as Image
        if (!image.isDisposed) {
            val x = event.x
            val y = event.y + event.ascent - style.metrics.ascent
            event.gc.drawImage(image, x, y)
        }
    }
}
