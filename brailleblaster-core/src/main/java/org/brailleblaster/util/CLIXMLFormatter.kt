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

import org.brailleblaster.utd.internal.xml.XMLHandler
import org.brailleblaster.utd.internal.xml.XMLHandler.Formatted
import java.io.File
import kotlin.system.exitProcess

/**
 * Format XML with XOM, much faster than sublime
 *
 *
 * Save this script
 * <pre>
 * Windows:
 *
 *
</pre> *
 *
 */
object CLIXMLFormatter {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 2) {
            System.err.println("<unformatted xml file> <dest>")
            exitProcess(1)
        }
        val input = File(args[0])
        val output = File(args[1])
        val xml: XMLHandler = Formatted()
        val doc = xml.load(input)
        xml.save(doc, output)
    }
}