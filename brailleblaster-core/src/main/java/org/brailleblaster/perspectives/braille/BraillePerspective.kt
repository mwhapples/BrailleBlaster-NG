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
package org.brailleblaster.perspectives.braille

import org.brailleblaster.perspectives.Controller
import org.brailleblaster.perspectives.Perspective
import org.brailleblaster.perspectives.braille.toolbar.BrailleToolBar
import org.brailleblaster.perspectives.braille.toolbar.CustomToolBarBuilder
import org.brailleblaster.wordprocessor.BBToolBar
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.custom.VerifyKeyListener

class BraillePerspective(val wp: WPManager) : Perspective() {
    override  val controller: Controller
        get() = wp.controller
    var keyListener: VerifyKeyListener? = null
        override val type: Class<*> = Manager::class.java
    override lateinit var toolBar: BBToolBar

    override fun initToolBar(wp: WPManager) {
        toolBar = BrailleToolBar(wp.shell, wp)
    }

    override fun buildToolBar() {
        (toolBar as BrailleToolBar).build()
    }

    fun addToToolBar(custTB: CustomToolBarBuilder) {
        (toolBar as BrailleToolBar).addToToolBar(custTB)
    }

    fun addToToolBar(custTB: CustomToolBarBuilder, keyListener: VerifyKeyListener?) {
        (toolBar as BrailleToolBar).addToToolBar(custTB)
        this.keyListener = keyListener
    }

    override fun rebuildToolBar(wp: WPManager) {
        //TODO: Without this null check will get NPE on linux
        if (::toolBar.isInitialized)
            toolBar.dispose()
        toolBar = BrailleToolBar(wp.shell, wp)
    }

    override fun dispose() {
        controller.dispose()
        toolBar.dispose()
    }
}