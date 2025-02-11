package org.brailleblaster.updates

import org.brailleblaster.CheckUpdates
import org.brailleblaster.perspectives.mvc.BBSimpleManager
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.AppStartedEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.MenuManager
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.tools.MenuTool
import org.eclipse.swt.widgets.Display

object CheckForUpdatesTool : MenuTool, BBSimpleManager.SimpleListener {
    override val topMenu: TopMenu = TopMenu.HELP
    override val title: String = "Check For Updates"
    override fun onRun(bbData: BBSelectionData) {
        Thread(CheckUpdates(true, Display.getCurrent())).start()
    }

    override fun onEvent(event: SimpleEvent) = when(event) {
        is AppStartedEvent -> Thread(CheckUpdates(false, Display.getCurrent())).start()
        is BuildMenuEvent -> MenuManager.addMenuItem(this)
        else -> {}
    }
}