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
package org.brailleblaster.utils.xom

import nu.xom.Builder
import nu.xom.Document
import nu.xom.Element
import nu.xom.Node
import nu.xom.ParsingException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object XOMSerializer {
    fun compress(n: Node): ByteArray? {
        val s = n.toXML()
        val baos = ByteArrayOutputStream()
        val gzipOut: GZIPOutputStream
        try {
            gzipOut = GZIPOutputStream(baos)
            val objectOut = ObjectOutputStream(gzipOut)
            objectOut.writeObject(s)
            objectOut.close()
            return baos.toByteArray()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }

    fun decompressElement(arr: ByteArray?): Element? {
        val s = decompress(arr)
        return if (s != null) toElement(s) else null
    }

    fun decompressDocument(arr: ByteArray?): Document? {
        val s = decompress(arr)
        return if (s != null) toDocument(s) else null
    }

    private fun decompress(arr: ByteArray?): String? {
        if (arr != null) {
            val bais = ByteArrayInputStream(arr)
            val gzipIn: GZIPInputStream
            try {
                gzipIn = GZIPInputStream(bais)
                val objectIn = ObjectInputStream(gzipIn)
                val s = objectIn.readObject() as String
                objectIn.close()
                return s
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun toDocument(s: String): Document? {
        try {
            val parser = Builder(false)
            return parser.build(s, null)
        } catch (ex: ParsingException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    private fun toElement(s: String): Element? {
        try {
            val parser = Builder(false)
            val doc = parser.build(s, null)
            val e = doc.rootElement
            return e.copy()
        } catch (ex: ParsingException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }
}