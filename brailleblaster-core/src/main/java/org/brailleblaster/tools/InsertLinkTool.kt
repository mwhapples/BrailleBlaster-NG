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
package org.brailleblaster.tools

import nu.xom.Attribute
import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.utils.swt.EasySWT
import org.brailleblaster.utils.xom.childNodes
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Shell

private val localeHandler = LocaleHandler.getDefault()

class InsertLinkTool(parent: Shell) : Dialog(parent, SWT.NONE), MenuToolModule {
  override val topMenu: TopMenu = TopMenu.INSERT
  override val title: String = localeHandler["InsertLink"]
  override val accelerator: Int = SWT.MOD1 or 'K'.code

  override fun onRun(bbData: BBSelectionData) {
    //Get input from a simple dialog box
    val caretNode = bbData.manager.simpleManager.currentCaret.node
    val childNodes = caretNode.childNodes
    //Need to determine if the selection is valid for inserting a link
    //If a link already exists in the selection, fill the dialog box with that link
    var linkText = ""
    //If it's a Link BBX, get the href attribute and set linkText to that
    //If it's not, leave linkText as an empty string
    if (caretNode.equals(BBX.INLINE.LINK)) {
      //But how do we actually get the attribute value? Can't cast nu.xom.Text to nu.xom.Element
      println("Existing link found")
    }
    else{
      println("node xml: ${caretNode.toXML()}")
      println("Child nodes: ${childNodes.size}\n ${childNodes.run{ forEach { println(it.toXML())}}}" )
    }

    makeGUI(bbData, linkText)
  }

  private fun makeGUI(bbData: BBSelectionData, linkText: String) {
    val shell = Shell(
      parent.display,
      SWT.DIALOG_TRIM or SWT.RESIZE or SWT.CENTER
    )
    shell.text = localeHandler["InsertLink"]
    shell.layout = GridLayout(1, false)

    val inputGroup = EasySWT.makeGroup(shell, SWT.CENTER, 2, false)
    val entryText = EasySWT.makeText(inputGroup, 180, 2)
    entryText.text = linkText
    EasySWT.addEnterListener(entryText) {
      //Insert the link at the current selection
      val linkText = entryText.getText()
      insertExternalLink(linkText, bbData)
      shell.close()
    }

    val buttonsGroup = EasySWT.makeGroup(shell, SWT.CENTER, 3, true)
    EasySWT.makePushButton(buttonsGroup, localeHandler["InsertLink"], 1) {
      //Insert the link at the current selection (same behavior as pressing Enter in the text box)
      val linkText = entryText.getText()
      insertExternalLink(linkText, bbData)
      shell.close()
    }
    EasySWT.makePushButton(buttonsGroup, localeHandler["buttonCancel"], 1) {
      shell.close()
    }
    EasySWT.makePushButton(buttonsGroup, localeHandler["removeLink"], 1) {
      removeExternalLink(bbData)
      shell.close()
    }
    EasySWT.addEscapeCloseListener(shell)
    shell.pack()
    shell.open()
  }

  private fun insertExternalLink(link: String, bbData: BBSelectionData) {
    println("Inserting link text: $link")
    val mapList = bbData.manager.mapList
    val current = mapList.current

    if (current.isLink){
      //Modify existing link
      println("Modifying existing link (debug)")
      //current.linkhref
      //Do I need to do anything else here?
      //bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.NO_SENDER, false, caretNode))
      return
    }
    else {
      //Figure out some way to do something...
      println("Checking caret Node (debug)")
      return
    }
  }

  private fun removeExternalLink(bbData: BBSelectionData){
    val mapList = bbData.manager.mapList
    val current = mapList.current


    if (current.isLink){
      //caretElement.removeAttribute(caretElement.getAttribute("external"))
      //caretElement.removeAttribute(caretElement.getAttribute("href"))
      //bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.NO_SENDER, false, caretElement))
    }
    else {
      //If caret node is not an existing link, do we need to check the selection for one?

    }
  }
}