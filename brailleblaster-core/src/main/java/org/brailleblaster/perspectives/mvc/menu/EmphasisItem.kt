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

import org.brailleblaster.utils.localization.LocaleHandler.Companion.getDefault
import org.brailleblaster.utd.properties.EmphasisType

/**
 * Emphasis items that are shared between the menus and the toolbars
 */
enum class EmphasisItem(val emphasisType: EmphasisType) {
    BOLD(EmphasisType.BOLD),
    ITALIC(EmphasisType.ITALICS),
    UNDERLINE(EmphasisType.UNDERLINE),
    SCRIPT(EmphasisType.SCRIPT),
    TNSYMBOLS(EmphasisType.TRANS_NOTE),
    TD1(EmphasisType.TRANS_1),
    TD2(EmphasisType.TRANS_2),
    TD3(EmphasisType.TRANS_3),
    TD4(EmphasisType.TRANS_4),
    TD5(EmphasisType.TRANS_5);

    @JvmField
    val longName: String = getDefault()[emphasisType.longName]

}
