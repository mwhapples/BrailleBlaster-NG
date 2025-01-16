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
package org.brailleblaster.userHelp

import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.codehaus.plexus.util.xml.pull.XmlPullParserException
import java.io.File
import java.io.FileReader
import java.io.IOException

class MavenProperties internal constructor() {
    private val model: Model?
    private val bbModel: Model?
    private val utdModel: Model?
    fun getProperty(s: String?): String? {
        var version: Any?
        if (model != null) {
            version = model.properties[s]
            if (version != null) {
                return version.toString()
            }
        }
        if (bbModel != null) {
            version = bbModel.properties[s]
            if (version != null) {
                return version.toString()
            }
        }
        if (utdModel != null) {
            version = utdModel.properties[s]
            if (version != null) {
                return version.toString()
            }
        }
        return null
    }

    init {
        val reader = MavenXpp3Reader()
        model = if (File("../pom.xml").exists()) {
            try {
                reader.read(FileReader("../pom.xml"))
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                null
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                null
            }
        } else null
        bbModel = if (File("pom.xml").exists()) {
            try {
                reader.read(FileReader("pom.xml"))
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                null
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                null
            }
        } else null
        utdModel = if (File("../utd/pom.xml").exists()) {
            try {
                reader.read(FileReader("../utd/pom.xml"))
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                null
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                null
            }
        } else null
    }
}