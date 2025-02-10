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
package org.brailleblaster.app

import java.net.URL
import java.net.URLClassLoader

class BBClassLoader(name: String, parent: ClassLoader) : URLClassLoader(name, arrayOfNulls(0), parent) {
    @Suppress("unused")
    constructor(parent: ClassLoader) : this("BBClassLoader", parent)

    fun add(url: URL) {
        addURL(url)
    }
}