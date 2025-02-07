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
package org.brailleblaster.archiver2

import nu.xom.Document
import org.brailleblaster.archiver2.ArchiverFactory.ExtensionSupport
import org.brailleblaster.utd.UTDTranslationEngine
import java.io.Closeable
import java.nio.file.Path

/**
 * Represents an opened BBX document
 */
interface Archiver2 : ExtensionSupport, Closeable {
    /**
     * A fully converted BBX document
     * @return
     */
	val bbxDocument: Document

    /**
     * Find a relative file associated with the document
     * @return
     */
    fun resolveSibling(descendant: Path): Path

    /**
     * The BBX/Z file on disk
     * @return
     */
	val path: Path

    /**
     * Save to given path without changing origPath
     *
     *
     * Used for regular File > Save menu, and ArchiverRecoverThread
     * @param destPath
     * @param engine TODO
     * @param options The options for saving the document.
     */
    fun save(destPath: Path, doc: Document, engine: UTDTranslationEngine, options: Set<SaveOptions>)

    /**
     * Save to given path and change origPath
     * @param destPath
     * @param engine TODO
     * @param options The options for saving the document.
     */
    fun saveAs(destPath: Path, doc: Document, engine: UTDTranslationEngine, options: Set<SaveOptions>)

    /**
     * The path from where the document was imported from, null if the document was not imported.
     */
    var importedFrom: Path?

    /**
     * Set this archiver as not being imported from another document.
     *
     */
    fun setNotImported()
    val isImported: Boolean
        get() = importedFrom != null

    fun setNoNewPath()
	var newPath: Path?
}
