package org.brailleblaster.tools

import nu.xom.Element
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.XMLTextCaret
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.perspectives.mvc.events.XMLCaretEvent
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.utils.swt.EasySWT
import org.brailleblaster.utils.xml.BB_NS
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.List
import org.eclipse.swt.widgets.Shell

//This tool adds bookmark XML elements at the current block.
//The link manager then handles linking internal links to those bookmarks, much like MS Word
private val localeHandler = LocaleHandler.getDefault()

class InsertBookmarkTool(parent: Manager) : Dialog(parent.wpManager.shell, SWT.NONE), MenuToolModule {
  override val topMenu: TopMenu = TopMenu.NAVIGATE
  override val title: String = localeHandler["BookmarksMenu"]
  override val sharedItem: SharedItem = SharedItem.INSERT_BOOKMARK

  private var bookmarksTMEList = mutableListOf<TextMapElement>()

  override fun onRun(bbData: BBSelectionData) {
    val shell = Shell(parent.display)
    shell.text = "Bookmarks"
    shell.layout = GridLayout(2, false)
    
    val textBoxGroup = EasySWT.makeGroup(shell, SWT.NONE, 1, true)
    val buttonsGroup = EasySWT.makeGroup(shell, SWT.NONE, 1, true)

    val gd = GridData()
    gd.widthHint = 180
    gd.heightHint = 90
    gd.grabExcessVerticalSpace = true
    gd.grabExcessHorizontalSpace = true

    val entryBox = EasySWT.makeText(textBoxGroup, 180, 1)
    entryBox.message = "Enter a unique bookmark name"
    val bookmarksList = List(textBoxGroup, SWT.BORDER or SWT.V_SCROLL or SWT.SINGLE)
    getBookmarksList(bbData).forEach {
      bookmarksList.add(it)
    }
    //Add a double-click listener to the list to go to the selected bookmark
    bookmarksList.addListener(SWT.MouseDoubleClick) {
      if (bookmarksList.selectionIndex != -1) {
        moveToBookmark(bbData, bookmarksList.selectionIndex)
      }
    }
    bookmarksList.layoutData = gd
    bookmarksList.setFocus()

    EasySWT.addEnterListener(bookmarksList){
      if (bookmarksList.selectionIndex != -1) {
        //Navigate to existing bookmark if already present
        moveToBookmark(bbData, bookmarksList.selectionIndex)
        bookmarksList.deselectAll()
      }
    }

    EasySWT.addEnterListener(entryBox) {
      if (!entryBox.text.isEmpty() && !bookmarksList.items.contains(entryBox.text)) {
        addBookmark(bbData, entryBox.text)

        bookmarksList.deselectAll()
        //Regenerate bookmarks list
        bookmarksList.removeAll()
        getBookmarksList(bbData).forEach {
          bookmarksList.add(it)
        }
        bookmarksList.redraw()
        bookmarksList.update()
      }
    }

    val bookmarkAtCursor = Button(buttonsGroup, SWT.PUSH)
    bookmarkAtCursor.text = "Add Bookmark at Cursor"
    bookmarkAtCursor.addListener(SWT.Selection) {
      if (!entryBox.text.isEmpty() && !bookmarksList.items.contains(entryBox.text)) {
        addBookmark(bbData, entryBox.text)

        bookmarksList.deselectAll()
        //Regenerate bookmarks list
        bookmarksList.removeAll()
        getBookmarksList(bbData).forEach {
          bookmarksList.add(it)
        }
        bookmarksList.redraw()
        bookmarksList.update()
      }
    }

    EasySWT.makePushButton(buttonsGroup, "Remove Selected Bookmark", 1) {
      //remove currently selected bookmark
      removeBookmark(bbData, bookmarksList.selectionIndex)

      //Regenerate bookmarks list
      bookmarksList.deselectAll()
      bookmarksList.removeAll()
      getBookmarksList(bbData).forEach {
        bookmarksList.add(it)
      }
      bookmarksList.redraw()
      bookmarksList.update()
    }

    EasySWT.makePushButton(buttonsGroup, "Go To Selected Bookmark", 1) {
      if (bookmarksList.selectionIndex != -1) {
        moveToBookmark(bbData, bookmarksList.selectionIndex)
        bookmarksList.deselectAll()
      }
    }

    EasySWT.makePushButton(buttonsGroup, "Clear Selection", 1) {
      bookmarksList.deselectAll()
    }

    EasySWT.addEscapeCloseListener(shell)

    shell.pack()
    shell.open()
  }

  private fun removeBookmark(bbData: BBSelectionData, bookmarkIndex: Int) {
    //Remove the link pointer in the selected block with the given linkID.
    //Note that this does not remove any links that point to this bookmark...Does MS Word even do that?
    if (bookmarkIndex in bookmarksTMEList.indices) {
      val tme = bookmarksTMEList[bookmarkIndex]
      val el = tme.node.parent as Element
      el.removeAttribute(el.getAttribute("linkID", BB_NS))
      bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, el.parent))
    }
    else {
      return
    }
  }

  private fun addBookmark(bbData: BBSelectionData, newLinkID: String) {
    //Create a link "pointer" in a selected block with a unique name (provided by user)
    //Later I may want to have the manager or other high-level class maintain a linkID list for quick access.
    val mapList = bbData.manager.mapList
    val current = mapList.current

    if (current.block != null) {
      try {
        current.block!!.addAttribute(BBX.BLOCK.LINKID.newAttribute(newLinkID))
        bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, current.nodeParent))
        //println("Added $newLinkID to block: ${current.block!!.toXML()}")
      }
      catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  private fun moveToBookmark(bbData: BBSelectionData, bookmarkIndex: Int) {
    //Move the caret to the block that contains the bookmark with the given index in bookmarksTMEList
    if (bookmarkIndex in bookmarksTMEList.indices) {
      val tme = bookmarksTMEList[bookmarkIndex]
      bbData.manager.simpleManager.dispatchEvent(XMLCaretEvent(Sender.NO_SENDER, XMLTextCaret(tme.node as Text, 0)))
    }
  }

  //Generate the strings for the SWT List and populate bookmarksTMEList for later use
  private fun getBookmarksList(bbData: BBSelectionData): kotlin.collections.List<String> {
    val internalLinkNodes = bbData.manager.mapList.filter { m ->
      m.node != null && !(m.node.parent as Element).getAttributeValue("linkID", BB_NS).isNullOrEmpty()
    }
    bookmarksTMEList = internalLinkNodes as MutableList<TextMapElement>

    return internalLinkNodes.map {
      val el = it.node.parent as Element
      el.getAttributeValue("linkID", BB_NS).toString()
    }.toList()
  }
}