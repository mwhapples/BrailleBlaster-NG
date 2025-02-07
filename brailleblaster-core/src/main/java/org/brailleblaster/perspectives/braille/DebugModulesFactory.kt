/*
 * Copyright (C) 2025 Michael Whapples
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
package org.brailleblaster.perspectives.braille

import org.brailleblaster.frontmatter.TOCBuilderBBX
import org.brailleblaster.frontmatter.TPagesDialog
import org.brailleblaster.perspectives.braille.ui.SimpleImageDescriberModule
import org.brailleblaster.perspectives.mvc.BBSimpleManager
import org.brailleblaster.perspectives.mvc.modules.misc.AlphabeticReferenceModule
import org.brailleblaster.perspectives.mvc.modules.misc.VolumeChangeModule
import org.brailleblaster.perspectives.mvc.modules.misc.VolumeInsertModule
import org.brailleblaster.spi.ModuleFactory

class DebugModulesFactory : ModuleFactory {
    override fun createModules(manager: Manager): Iterable<BBSimpleManager.SimpleListener> = listOf(
        TPagesDialog(),
        VolumeInsertModule(),
        TOCBuilderBBX(manager),
        AlphabeticReferenceModule(manager),
        SimpleImageDescriberModule,
        VolumeChangeModule(manager),
    )
}