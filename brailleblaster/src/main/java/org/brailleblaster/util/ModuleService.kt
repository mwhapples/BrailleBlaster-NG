package org.brailleblaster.util

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.spi.ModuleFactory
import java.util.*

class ModuleService {
    private val serviceLoader: ServiceLoader<ModuleFactory> = ServiceLoader.load(ModuleFactory::class.java)
    val moduleFactories: Sequence<ModuleFactory>
        get() = serviceLoader.asSequence()
    fun modules(manager: Manager): Sequence<SimpleListener> = moduleFactories.flatMap { it.createModules(manager) }.asSequence()
}