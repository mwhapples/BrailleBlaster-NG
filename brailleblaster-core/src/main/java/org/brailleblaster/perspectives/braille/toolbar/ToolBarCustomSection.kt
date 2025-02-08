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
package org.brailleblaster.perspectives.braille.toolbar

import org.brailleblaster.perspectives.mvc.ViewManager.Companion.colorizeCustomToolbars
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem

class ToolBarCustomSection(private val tbBuilder: CustomToolBarBuilder, private val compParent: Composite) :
    IToolBarElement {
    private var newParent: Composite? = null

    /**
     * Note: sharedToolBars is not used by custom toolbars and should be null
     */
    override fun createSection(parent: ToolBar, sharedToolBars: MutableMap<SharedItem, ToolItem>): ToolBar {
//		newParent = EasySWT.makeComposite(compParent, tbBuilder.widgets.size());
        newParent = EasySWT.buildComposite(compParent).apply {
            this.columns = tbBuilder.widgets.size
        }.build().apply {
            EasySWT.buildGridData().setColumns(tbBuilder.widgets.size).setGrabSpace(
                horizontally = true,
                vertically = false
            )
                .setAlign(SWT.FILL, SWT.FILL).applyTo(this)
            tbBuilder.build(this)
            colorizeCustomToolbars(this)
            //		newParent.pack(true);
            compParent.layout(true)
            //		compParent.redraw();
        }
        return parent
    }

    fun dispose() {
        newParent!!.dispose()
    }

    val height: Int
        get() = newParent!!.bounds.height
}
