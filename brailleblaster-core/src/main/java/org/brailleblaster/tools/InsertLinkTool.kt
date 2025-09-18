package org.brailleblaster.tools

import nu.xom.Element
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.utils.swt.EasySWT
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
    val caretNode = bbData.manager.simpleManager.currentCaret.node as Element
    //Need to determine if the selection is valid for inserting a link
    //If a link already exists in the selection, fill the dialog box with that link
    var linkText = ""
    //Listen Jack, how do I mess with BBXs?
    //If it's a Link BBX, get the href attribute and set linkText to that
    //If it's not, leave linkText as an empty string
    if (caretNode.equals(BBX.INLINE.LINK) && caretNode.getAttribute("external")?.value == "true"){
      linkText = caretNode.getAttributeValue("href") ?: ""
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

    val buttonsGroup = EasySWT.makeGroup(shell, SWT.CENTER, 2, true)
    EasySWT.makePushButton(buttonsGroup, localeHandler["buttonOk"], 1)
    {
      //Insert the link at the current selection (same behavior as pressing Enter in the text box)
      val linkText = entryText.getText()
      insertExternalLink(linkText, bbData)
      shell.close()
    }
    EasySWT.makePushButton(buttonsGroup, localeHandler["buttonCancel"], 1)
    {
      //Close dialog
      shell.close()
    }
    EasySWT.addEscapeCloseListener(shell)
    shell.pack()
    shell.open()
  }

  private fun insertExternalLink(link: String, bbData: BBSelectionData) {
    println("Link text: $link")
    val mapList = bbData.manager.mapList
    val sel = bbData.manager.simpleManager.currentSelection
    val start = sel.start
    val end = sel.end
    val caretNode = bbData.manager.simpleManager.currentCaret.node as Element

    if (caretNode.equals(BBX.INLINE.LINK) && caretNode.getAttribute("external")?.value == "true"){
      //Modify existing link
      BBX.INLINE.LINK.ATTRIB_HREF[caretNode] = link
      //Do I need to do anything else here?
      bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, caretNode.parent))
      return
    }

    //TODO add code for modifying the Link subtype of the BBX node (InlineElement)
    //When inserting the link, if linkText is empty, create a new Link BBX
    //If it's not empty, modify the existing Link BBX to have the new href attribute

    //Reference Clipboard - Paste function for this?
    //It won't be exactly the same, but it will be similar.

    //Question: how are links going to be displayed?
    //Text view is easy, relatively: make the selection blue and underlined, make the hover text show the URL
    //Internal links might be more difficult - maybe don't show any tooltip, just the blue underline?
    //Will need a link manager GUI to deal with internal ones and where they point - separate class.
    //No idea what to do for braille view yet, if anything.

  }
}