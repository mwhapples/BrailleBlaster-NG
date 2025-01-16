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
package org.brailleblaster.util

import java.io.FilterInputStream
import java.io.FilterOutputStream
import java.io.InputStream
import java.io.OutputStream

class PageFilterInputStream(inputStream: InputStream) : FilterInputStream(inputStream) {
    var pageNumber: Int = 0
    private var markPageNumber: Int = 0
    override fun read(): Int {
        val c = super.read()
        if (c == 0xc) {
            pageNumber++
        }
        return c
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val i = super.read(b, off, len)
        pageNumber += b.slice(off until (off + i)).count { it.toInt() == 0xc }
        return i
    }

    override fun skip(n: Long): Long {
        if (n <= 0) {
            return 0
        }
        var remaining = n
        val chunk = 2048L
        val data = ByteArray(chunk.toInt())
        var nr: Int
        while (remaining > 0) {
            nr = read(data, 0, remaining.coerceAtMost(chunk).toInt())
            if (nr < 0) {
                break
            } else {
                remaining -= nr
            }
        }
        return n - remaining
    }

    override fun mark(readlimit: Int) {
        markPageNumber = pageNumber
        super.mark(readlimit)
    }

    override fun reset() {
        pageNumber = markPageNumber
        super.reset()
    }
}

class PageFilterOutputStream(outputStream: OutputStream) : FilterOutputStream(outputStream) {
    var pageNumber: Int = 0
    override fun write(b: Int) {
        if (b == 0xc) {
            pageNumber++
        }
        out.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        pageNumber += b.slice(off until (off + len)).count { it.toInt() == 0xc }
        out.write(b, off, len)
    }
}