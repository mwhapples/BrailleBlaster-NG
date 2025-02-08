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

import org.brailleblaster.utils.localization.LocaleHandler.Companion.getDefault

object SearchConstants {

  private val localeHandler = getDefault()
  @JvmField
  val SEARCH = localeHandler["&Search"]
  @JvmField
  val REPEAT_SEARCH = localeHandler["&RepeatSearch"]
  @JvmField
  val CASE_SENSITIVE = localeHandler["caseSensitive"]
  @JvmField
  val WHOLE_WORD = localeHandler["wholeWord"]
  @JvmField
  val REPLACE_WITH = localeHandler["replaceWith"]
  @JvmField
  val MATCH_CASE = localeHandler["matchCase"]
  @JvmField
  val REPLACE = localeHandler["fnr.replace"]
  @JvmField
  val REPLACE_FIND = localeHandler["replaceFind"]
  @JvmField
  val REPLACE_ALL = localeHandler["replaceAll"]
  @JvmField
  val FORMATTING = localeHandler["formatting"]
  @JvmField
  val FORWARD = localeHandler["forward"]
  @JvmField
  val BACKWARD = localeHandler["backward"]
  @JvmField
  val RESET = localeHandler["fnr.reset"]
  @JvmField
  val CLOSE = localeHandler["fnr.close"]
  @JvmField
  val CONTAINER_WARNING = localeHandler["fnr.containerWarning"]
  @JvmField
  val NO_SEARCHES_IN_MEMORY = localeHandler["fnr.noSearchesInMemory"]
  @JvmField
  val FIND_REPLACE_SHELL = localeHandler["fnrShell"]

  //Formatting dialog
  @JvmField
  val STYLES_AND_CONTAINERS = localeHandler["stylesAndContainers"]
  @JvmField
  val STYLES = localeHandler["styles"]
  @JvmField
  val ADD = localeHandler["fnr.add"]
  @JvmField
  val REMOVE = localeHandler["fnr.remove"]
  @JvmField
  val TEXT_ATTRIBUTES = localeHandler["textAttributes"]
  @JvmField
  val FIND = localeHandler["fnr.find"]
  @JvmField
  val MODIFY = localeHandler["fnr.modify"]
  @JvmField
  val CANNOT_SELECT_STYLE_AND_CONTAINER = localeHandler["cannotSelectStyleAndContainer"]
  @JvmField
  val CANNOT_REMOVE_STYLE = localeHandler["cannotRemoveStyle"]
  @JvmField
  val ERROR_REMOVING_CONTAINER = localeHandler["errorRemovingContainer"]
  @JvmField
  val DONE = localeHandler["fnr.done"]
  @JvmField
  val CANCEL = localeHandler["fnr.cancel"]
  @JvmField
  val OK = localeHandler["lblOk"]

  //Search notices
  @JvmField
  val END_DOC_SHELL = localeHandler["endOfDocument"]
  @JvmField
  val BEGIN_DOC = localeHandler["beginOfDocument"]
  @JvmField
  val RESET_END_BEGIN = localeHandler["resetEndBegin"]
  @JvmField
  val REPLACE_ALL_COMPLETE = localeHandler["replaceAllComplete"]
  const val INSTANCES_REPLACED = "instancesReplaced"
  const val REPLACE_ALL_TABLE_WARNING = "replaceAllTableWarning"
  @JvmField
  val WORD_NOT_FOUND = localeHandler["wordNotFound"]
}