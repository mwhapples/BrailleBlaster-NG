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
package org.brailleblaster.bbx.fixers

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import nu.xom.Node
import org.brailleblaster.utd.internal.InterfaceAdapter

/**
 * This takes a BBX document from a 1-to-1 $sourceFormat to BBX import
 * and cleans up the structure so the braille formatting makes sense
 */
@XmlJavaTypeAdapter(InterfaceAdapter::class)
interface ImportFixer {
    fun fix(matchedNode: Node)
}