package org.brailleblaster.updates

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager
import org.brailleblaster.spi.ModuleFactory

class UpdatesModuleFactory : ModuleFactory {
    override fun createModules(manager: Manager): Iterable<BBSimpleManager.SimpleListener> = listOf(CheckForUpdatesTool)
}