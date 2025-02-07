package org.brailleblaster.debug

import org.brailleblaster.frontmatter.TOCBuilderBBX
import org.brailleblaster.frontmatter.TPagesDialog
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.ui.SimpleImageDescriberModule
import org.brailleblaster.perspectives.mvc.BBSimpleManager
import org.brailleblaster.perspectives.mvc.modules.misc.AlphabeticReferenceModule
import org.brailleblaster.perspectives.mvc.modules.misc.VolumeChangeModule
import org.brailleblaster.perspectives.mvc.modules.misc.VolumeInsertModule
import org.brailleblaster.spi.ModuleFactory

class DebugModulesFactory : ModuleFactory {
    override fun createModules(manager: Manager): Iterable<BBSimpleManager.SimpleListener> = buildList {
        addAll(DebugTool.tools)
        add(TPagesDialog())
        add(VolumeInsertModule())
        add(TOCBuilderBBX(manager))
        add(AlphabeticReferenceModule(manager))
        add(SimpleImageDescriberModule)
        add(VolumeChangeModule(manager))
    }
}
