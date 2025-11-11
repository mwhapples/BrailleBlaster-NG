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

import nu.xom.Element
import nu.xom.Text
import org.brailleblaster.bbx.BBX
import org.brailleblaster.bbx.findBlock
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.braille.views.wp.TextView
import org.brailleblaster.perspectives.mvc.XMLTextCaret
import org.brailleblaster.perspectives.mvc.events.ModifyEvent
import org.brailleblaster.utils.localization.LocaleHandler
import org.brailleblaster.perspectives.mvc.menu.BBSelectionData
import org.brailleblaster.perspectives.mvc.menu.SharedItem
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.utd.internal.xml.XMLHandler
import org.brailleblaster.utils.xml.BB_NS
import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Dialog
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.widgets.List
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.TabFolder
import org.eclipse.swt.widgets.TabItem
import org.slf4j.LoggerFactory


private val localeHandler = LocaleHandler.getDefault()

class InsertLinkTool(parent: Shell) : Dialog(parent, SWT.NONE), MenuToolModule {
  override val topMenu: TopMenu = TopMenu.INSERT
  override val title: String = localeHandler["InsertLink"]
  override val accelerator: Int = SWT.MOD1 or 'K'.code
  override val sharedItem: SharedItem = SharedItem.INSERT_LINK

  private val logger = LoggerFactory.getLogger(TextView::class.java)
  private var bookmarksTMEList = mutableListOf<TextMapElement>()

  override fun onRun(bbData: BBSelectionData) {
    //Get input from a simple dialog box
    val current = bbData.manager.mapList.current
    //If a link already exists in the selection, fill the dialog box with that link
    //Only applies to external links - internal links are handled in separate menu tab
    var linkText = ""
    val el = current.nodeParent
    if (el != null && BBX.INLINE.LINK.isA(el)
      && el.getAttributeValue("external", BB_NS).toBoolean()) {
      //If it's a Link BBX, get the href attribute and set linkText to that
      //If it's not, leave linkText as an empty string
      linkText = el.getAttributeValue("href", BB_NS)
    }
    makeGUI(bbData, linkText)
  }

  private fun makeGUI(bbData: BBSelectionData, linkText: String) {
    val shell = Shell(parent.display)
    shell.text = localeHandler["InsertLink"]
    shell.layout = FillLayout()

    val tabs = TabFolder(shell, SWT.NONE)
    val tabExternal = TabItem(tabs, SWT.NONE)
    tabExternal.text = "External Links"
    val tabInternal = TabItem(tabs, SWT.NONE)
    tabInternal.text = "Internal Links"

    val externalTabGroup = Group(tabs, SWT.NONE)
    externalTabGroup.layout = GridLayout(2, false)
    val internalTabGroup = Group(tabs, SWT.NONE)
    internalTabGroup.layout = GridLayout(2, false)

    val entryText = EasySWT.makeText(externalTabGroup, 180, 2)
    entryText.text = linkText
    EasySWT.makePushButton(externalTabGroup, localeHandler["InsertLink"], 1) {
      //Insert the link at the current selection (same behavior as pressing Enter in the text box)
      insertLink(entryText.text, true, bbData)
      shell.close()
    }
    EasySWT.makePushButton(externalTabGroup, localeHandler["removeLink"], 1) {
      removeLink(bbData)
      shell.close()
    }
    EasySWT.makePushButton(externalTabGroup, localeHandler["buttonCancel"], 1) {
      shell.close()
    }
    tabExternal.control = externalTabGroup

    val bookmarkList = List(internalTabGroup, SWT.BORDER or SWT.V_SCROLL or SWT.SINGLE)
    bookmarkList //Set list with names of bookmarks in document; subsequent buttons will use the selected bookmark
    getBookmarksList(bbData).forEach {
      bookmarkList.add(it)
    }
    EasySWT.makePushButton(internalTabGroup, "Set Internal Link", 1) {
      //Set a link pointer at the current block based on the selected bookmark
      if (bookmarkList.selection.toString().isEmpty()){
        //No selection - do nothing. Should have a notice to the user here eventually.
      }
      else {
        setInternalLinkID(bookmarkList.selection.toString(), bbData)
      }
      shell.close()
    }
    EasySWT.makePushButton(internalTabGroup, "Remove Internal Link", 1) {
      //Works the same for internal and external links
      removeLink(bbData)
      //shell.close()
    }
    EasySWT.makePushButton(internalTabGroup, localeHandler["buttonCancel"], 1) {
      shell.close()
    }
    tabInternal.control = internalTabGroup

    EasySWT.addEscapeCloseListener(shell)

    shell.pack()
    shell.open()
  }

  private fun insertLink(link: String, isExternal: Boolean, bbData: BBSelectionData) {
    //println("Inserting link text: $link")
    val mapList = bbData.manager.mapList
    val current = mapList.current

    if (current.nodeParent != null && BBX.INLINE.LINK.isA(current.nodeParent)) {
      //Currently on an existing link - modify it
      //Process is same for internal and external links - the external attribute has already been set
      //println("Modifying existing link")
      if (link.isNotEmpty()) { //Might want to validate the link format here
        current.nodeParent.addAttribute(BBX.INLINE.LINK.ATTRIB_HREF.newAttribute(link))
        bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, current.nodeParent))
      }
      return
    }
    else {
      //Not on a link - create one if there's a valid selection
      if (current.isReadOnly || bbData.manager.simpleManager.currentSelection.isTextNoSelection) {
        //Do not allow link creation in read-only areas or if there's no selection
        //println("No selection or read-only area - not creating link")
        return
      }
      if (!bbData.manager.simpleManager.currentSelection.isSingleNode) {
        //println("Multiple nodes selected - cannot create link")
        return
      }

      //println("Single node selected - creating link")
      val newLink = BBX.INLINE.LINK.create()
      newLink.addAttribute(BBX.INLINE.LINK.IS_EXTERNAL.newAttribute(isExternal))
      newLink.addAttribute(BBX.INLINE.LINK.ATTRIB_HREF.newAttribute(link))

      val currentNode = bbData.manager.simpleManager.currentCaret.node

      if (currentNode !is Text) {
        logger.debug("Current node is not text - cannot create link")
        return
      }

      val nodeLength = currentNode.value.length
      val start = (bbData.manager.simpleManager.currentSelection.start as XMLTextCaret).offset
      val end = (bbData.manager.simpleManager.currentSelection.end as XMLTextCaret).offset
      //println("Adding link to Node ${currentNode.value}, length: $nodeLength, start: $start, end: $end")

      //We want either the whole node, or a portion of it.
      //That portion might be from the start to somewhere in the middle, two middle points, or middle to end.
      //So 4 cases, and the return size of splitTextNode varies accordingly.
      val nodeToWrap =
        if (start > 0 && end != -1 && end != nodeLength) {
          //get middle and wrap (two middle points)
          val splitTextNode = XMLHandler.splitTextNode(currentNode, start, end)
          splitTextNode[1]
        } else if (start > 0) {
          //get last and wrap (middle to very end of node)
          val splitTextNode = XMLHandler.splitTextNode(currentNode, start)
          splitTextNode[1]
        } else if (end != -1 && end != nodeLength) {
          //get beginning and wrap (start to middle)
          val splitTextNode = XMLHandler.splitTextNode(currentNode, end)
          splitTextNode[0]
        } else {
          //wrap all (start to very end)
          currentNode
        }
      XMLHandler.wrapNodeWithElement(nodeToWrap, newLink)
      //That got it!
      bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, true, nodeToWrap.parent))
    }
    return
  }

  private fun getInternalID(currentLink: String, bbData: BBSelectionData) {
    val maplist = bbData.manager.mapList
    val current = maplist.current
    //Need to add a linkID attribute to BBX.Block - also need some kind of link management to go with it
    //Either in manager class or similarly high up. Want it as simple as possible - an incrementing integer should be fine
    //Just need a reliable way to count existing linkIDs in the document when it loads, and increment from there.
    //Inserting internal link then pegs the linkID to the inline link's href attribute
    //That way the link is contained within a node that gets saved with the document
    //Then we need a way to jump to that linkID from the link - presumably in TextView.
    //Might be slow since we'll have to search the maplist for it.
    //Maybe some way to cache the list in manager? Then it's a slower operation to add/remove links, but faster to jump to them.
    println("node.toXML:" + current.node.findBlock().toXML())
    println("node.toString: " + current.node.toString())

    //there is a method for jumping the selection to a line, right?
    //From GoToPageDialog:
    //m.simpleManager.dispatchEvent(XMLCaretEvent(Sender.GO_TO_PAGE, XMLTextCaret(node, 0)))
    //getting the node is the hard part...maybe just use the maplist index?
    // Will have to do a few checks, and accept the fact that things will get weird if the document changes.
    //Either that or have the link manager monitor changes and update the link targets as needed...big nuisance.
    //That would have to be a core feature of the document manager, not just the link tool.

    //println("Current hash: ${current.hashCode()}")
    //println("Current nodeparent hash: ${current.nodeParent.hashCode()}")
    //Can't use node hashes for internal ID - they change every time the document loads.
    //How about absolute positions? Not dynamic, but at least stable across sessions.

  }

  private fun setInternalLinkID(currentLink: String, bbData: BBSelectionData) {
    //Create a link "pointer" at the current selection. The linkID must match a bookmark with the given linkID.
    //The bookmarksTMEList is populated when the dialog box is created,
    // and should always match what the list in the menu shows.

    for (tme in bookmarksTMEList){
      val el = tme.node.parent as Element
      val linkID = el.getAttributeValue("linkID", BB_NS).toString()
      if (linkID == currentLink){
        //println("Found bookmark element: ${el.toXML()}")
        //Set the link pointer
        insertLink(currentLink, false, bbData)
        return
      }
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

  private fun removeLink(bbData: BBSelectionData) {
    val mapList = bbData.manager.mapList
    val current = mapList.current
    if (current.nodeParent != null && BBX.INLINE.LINK.isA(current.nodeParent)) {
      //Wow, this part was way easier than I thought it would be
      XMLHandler.unwrapElement(current.nodeParent)
      bbData.manager.simpleManager.dispatchEvent(ModifyEvent(Sender.TEXT, false, current.nodeParent))
    }
  }

}
