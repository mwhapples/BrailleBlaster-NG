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
package org.brailleblaster.perspectives.mvc.menu

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.MenuItem

/**
 * Tracks horizontal lines added under menus
 */
class BBSeparator internal constructor(override val menu: TopMenu?) : IBBMenu {

    override fun build(parentMenu: Menu): MenuItem {
        return MenuItem(parentMenu, SWT.SEPARATOR)
    }

    override fun copy(): BBSeparator {
        return BBSeparator(menu)
    }
}
