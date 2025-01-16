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
package org.brailleblaster.perspectives.braille.viewInitializer

import org.brailleblaster.archiver2.Archiver2
import org.brailleblaster.perspectives.braille.document.BrailleDocument
import org.brailleblaster.perspectives.braille.views.wp.BrailleView
import org.brailleblaster.perspectives.braille.views.wp.TextView

object ViewFactory {
    @JvmStatic
	fun createUpdater(arch: Archiver2, doc: BrailleDocument, text: TextView, braille: BrailleView): ViewInitializer {
//		if(arch instanceof EPub3Archiver || (arch instanceof UTDArchiver && arch.getCurrentConfig().equals("epub.cfg")))
//			return new EPubInitializer(doc, text, braille, tree);
//		else if(arch instanceof WebArchiver || arch instanceof TextArchiver)
//			return new WebInitializer(doc, text, braille, tree);
//		else
        //TODO: Nimas and Epub archivers are essentially the same, nimas is also tested more. Assume this is all we need?
        return NimasInitializer(doc, text, braille)
    }
}
