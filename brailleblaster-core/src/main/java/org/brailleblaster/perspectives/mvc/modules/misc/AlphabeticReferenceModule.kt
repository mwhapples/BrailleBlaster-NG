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
package org.brailleblaster.perspectives.mvc.modules.misc

import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.MenuManager.addSubMenu
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.SubMenuBuilder
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.perspectives.mvc.modules.misc.AdjustGlossarySpacing.open
import org.brailleblaster.perspectives.mvc.modules.views.DebugModule
import org.brailleblaster.util.UncontractedGlossary

class AlphabeticReferenceModule(var manager: Manager) : SimpleListener {
    override fun onEvent(event: SimpleEvent) {
        if (event is BuildMenuEvent && DebugModule.enabled) {
            val smb = SubMenuBuilder(
                TopMenu.DEBUG,
                "Alphabetic Reference"
            )
            smb.addItem(UNCONTRACTED_GLOSSARY, 0) { e: BBSelectionData -> UncontractedGlossary.addUncontractedWord(e) }
            smb.addItem(IDENTIFY_GUIDE_WORDS, 0) {
                FindGuideWordModule.findGuideWords(
                    null,
                    manager
                )
            }
            smb.addItem(TERM_SPACING, 0) {
                open(
                    manager, manager.wpManager.shell
                )
            }
            smb.addItem(EDIT_GUIDE_WORD, 0, 0, {
                GuideWordEditor.open(
                    manager
                )
            }, SharedItem.EDIT_GUIDE_WORD)
            addSubMenu(smb)
        }
    }

    companion object {
        var UNCONTRACTED_GLOSSARY = "Add/Remove Uncontracted Glossary Items"
        var IDENTIFY_GUIDE_WORDS = "Identify Guide Words"
        var TERM_SPACING = "Adjust Term/Definition Spacing"
        @JvmField
		var EDIT_GUIDE_WORD = "Edit Guide Word"
    }
}