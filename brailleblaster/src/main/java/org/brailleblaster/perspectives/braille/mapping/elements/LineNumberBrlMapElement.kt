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
import org.brailleblaster.abstractClasses.BBEditorView
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.views.wp.WPView
import org.brailleblaster.wordprocessor.FontManager.Companion.copyViewFont
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.GC
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener

class LineNumberBrlMapElement : PaintedBrlOnlyBrlMapElement {
    var listener: Listener? = null

    constructor(start: Int, end: Int, node: Node) : super(start, end, node)

    constructor(node: Node) : super(node)

    fun startListener(manager: Manager, view: WPView, pos: Int) {
        if (listener != null) view.view.removeListener(SWT.Paint, listener)
        listener = Listener { event: Event ->
            val printText = parseLineNumber()
            val gc = GC(view.view)
            val line = view.view.getLineAtOffset(pos)
            val y1 = view.view.getLinePixel(line)
            val x2 = view.view.bounds.width
            val textWidth = gc.stringExtent(printText).x

            copyViewFont(manager, view, gc)

            event.gc.drawText(printText, x2 - (textWidth + 100), y1 + 5)
            gc.dispose()
        }
        view.view.addListener(SWT.Paint, listener)
    }

    override fun removeListener(view: BBEditorView) {
        if (listener != null) view.view.removeListener(SWT.Paint, listener)
    }

    private fun parseLineNumber(): String {
        var text = node.value
        val num = StringBuilder()
        val index = text.indexOf('#')
        if (index != -1) {
            run {
                var i = 0
                while (i < index) {
                    if (text[i] == ',') {
                        num.append(text[i + 1].uppercaseChar())
                        i++
                    } else num.append(text[i])
                    i++
                }
            }

            for (i in index + 1 until text.length) {
                if (text[i] == 'j') num.append('0')
                else if (text[i] == '-') num.append('-')
                else if (text[i] != '#') num.append((text[i].code - 'a'.code) + 1)
            }
        } else {
            text = text.replace(";", "")

            var i = 0
            while (i < text.length) {
                if (text[i] == ',') {
                    num.append(text[i + 1].uppercaseChar())
                    i++
                } else num.append(text[i])
                i++
            }
        }

        return num.toString()
    }
}
