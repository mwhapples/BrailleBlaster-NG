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
package org.brailleblaster.document

import nu.xom.Document
import org.brailleblaster.utils.xom.XOMSerializer

class DocumentSnapshot(doc: Document) {
    private var serializedDoc: ByteArray

    init {
        serializedDoc = compress(doc)!!
    }

    val snapshot: Document
        get() = decompress(serializedDoc)!!

    private fun compress(doc: Document): ByteArray? {
        return XOMSerializer.compress(doc)
    }

    private fun decompress(arr: ByteArray): Document? {
        return XOMSerializer.decompressDocument(arr)
    }
}