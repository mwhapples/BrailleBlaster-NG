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
package org.brailleblaster.math.mathml

import net.sourceforge.jeuclid.LayoutContext
import net.sourceforge.jeuclid.converter.Converter
import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.graphics.PaletteData
import org.slf4j.LoggerFactory
import org.w3c.dom.Node
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.IOException

object MathRenderer {
    private const val COLOR_ENTRIES = 3
    private const val BITS_PER_PIXEL = COLOR_ENTRIES * 8
    private val PALETTE_BGR = PaletteData(0xff, 0xff00, 0xff0000)
    private val logger = LoggerFactory.getLogger(MathRenderer::class.java)
    private val converter = Converter.getInstance()
    fun render(document: Node?, layoutContext: LayoutContext): ImageData? {
        return if (document != null) {
            try {
                val bi = converter.render(document, layoutContext, BufferedImage.TYPE_3BYTE_BGR)
                val r = bi.raster
                val b = r.dataBuffer as DataBufferByte
                val data = b.data
                val w = bi.width
                ImageData(w, bi.height, BITS_PER_PIXEL, PALETTE_BGR, COLOR_ENTRIES * w, data)
            } catch (e: IOException) {
                logger.warn(e.message, e)
                null
            }
        } else null
    }
}