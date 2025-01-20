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
package org.brailleblaster.search

import nu.xom.Node
import org.brailleblaster.perspectives.braille.Manager

class Find {

  var clickCount: Int = 0

  fun find(m: Manager, click: Click): Boolean {
    val domCon = DOMControl(click)
    val viewCon = ViewControl(m, click)
    return find(m, click, domCon, viewCon)
  }

  fun find(m: Manager, click: Click, domCon: DOMControl, viewCon: ViewControl): Boolean {
    //println("Find() called: ClickCount: " + clickCount)
    clickCount++

    //Further screening for invalid searches
    if (!SearchUtils.hasFindCriteria(click)) {
      return false
    }

    if (!click.settings.findHasText()) {
      var correctNode: Node? = null

      //If there are no nodes with matching attributes, return false
      if (domCon.possiblesCorrectAttributes.isEmpty()) {
        return false
      }
      //If the
      else if (!(domCon.possiblesCorrectAttributes[0] == click.initialView.mapElement.node
            && click.initialView.cursorOffset > click.initialView.mapElement.getStart(m.mapList))
      ) {

        correctNode = domCon.possiblesCorrectAttributes[0]
      } else if (domCon.possiblesCorrectAttributes.size > 1) {

        correctNode = domCon.possiblesCorrectAttributes[1]
      }
      if (correctNode != null) {
        if (correctNode.value.isNotEmpty()) {
          viewCon.findNodeAndHighlight(correctNode)
          return true
        }
      }
      return false
    } else {
      val totalSections = m.sectionList.size
      for (i in click.initialView.section until totalSections) {
        // find the section with the first text match. searching
        // by view is way too long, only do it once.
        val array: List<Node> =
          if (i == click.initialView.section) {
            // start with current node
            viewCon.nodeFromCurrent
          } else {
            // start at the beginning
            viewCon.getNodeFromBeginning(i)
          }

        val s1 = domCon.searchNoView(array)
        val s2 = domCon.searchNoViewNoSpaces(array)
        if (SearchUtils.matchPhrase(s1, click, false).hasNext()
          || SearchUtils.matchPhrase(s2, click, false).hasNext()
        ) {
          if (viewCon.findInSection(i, domCon.possiblesCorrectAttributes)) {
            return true
          }
        }
      }
      return false
    }
  }

}