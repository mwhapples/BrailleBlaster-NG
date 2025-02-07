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