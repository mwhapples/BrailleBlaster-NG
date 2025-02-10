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
package org.brailleblaster.bbx.utd

import nu.xom.Element
import nu.xom.Node
import org.brailleblaster.bbx.BBX
import org.brailleblaster.libembosser.utils.BrailleMapper
import org.brailleblaster.utd.TextSpan
import org.brailleblaster.utd.actions.BaseEmphasisAction
import org.brailleblaster.utd.properties.EmphasisType
import java.util.*

/**
 * Build emphasis bits from the emphasis attribute
 */
class BBXEmphasisAction : BaseEmphasisAction() {
    override val emphasis: EnumSet<EmphasisType> = EnumSet.noneOf(EmphasisType::class.java)
    override fun processEmphasis(origNode: Node?, processedInput: TextSpan) {
        BBX.INLINE.EMPHASIS.assertIsA(origNode)
        val origElem = origNode as Element?
        val elemEmphasis = BBX.INLINE.EMPHASIS.ATTRIB_EMPHASIS[origElem]
        // When the emphasis includes NO_TRANSLATE change the input to unicode Braille
        // to work around the LibLouis problem with correct opcode rules.
        if (elemEmphasis.contains(EmphasisType.NO_TRANSLATE)) {
            processedInput.text = BrailleMapper.ASCII_TO_UNICODE_FAST.map(processedInput.text)
        }
        processedInput.emphasis.addAll(
            elemEmphasis
        )
    }
}