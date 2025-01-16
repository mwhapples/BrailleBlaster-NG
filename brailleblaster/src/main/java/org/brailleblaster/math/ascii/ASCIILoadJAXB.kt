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
package org.brailleblaster.math.ascii

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import org.brailleblaster.BBIni
import org.brailleblaster.utd.config.UTDConfig
import java.io.File
import java.nio.file.Paths
import java.util.*

class ASCIILoadJAXB {
  var content = ASCIIEntries()
  var categories = ArrayList<String>()
    get() {
        for (i in content.entries.indices) {
          val category = content.entries[i].category
          if (!field.contains(category)) {
            field.add(category)
          }
        }
        field.sort()
        return field
    }

  init {
    try {
      content = JAXBContext.newInstance(ASCIIEntries::class.java).let {
        UTDConfig.loadJAXB(ASCIISETTINGS, ASCIIEntries::class.java, it)
      }
      categories
    } catch (e: JAXBException) {
      // TODO Auto-generated catch block
      e.printStackTrace()
    }
  }

  fun getEntries(name: String): ArrayList<ASCIIEntry> {
    val array = ArrayList<ASCIIEntry>()
    for (i in content.entries.indices) {
      if (content.entries[i].category == name) {
        array.add(content.entries[i])
      }
    }
    array.sortWith(Comparators.ENTRY)
    return array
  }

  object Comparators {
    val ENTRY = java.util.Comparator { o1: ASCIIEntry, o2: ASCIIEntry -> o1.name.compareTo(o2.name) }
    val CATEGORY = java.util.Comparator { o1: ASCIICategory, o2: ASCIICategory ->
      o2
        .prettyName.compareTo(o1.prettyName)
    }
  }

  companion object {
    val ASCIISETTINGS: File = BBIni.programDataPath.resolve(Paths.get("settings", "asciicheat.xml")).toFile()
  }
}