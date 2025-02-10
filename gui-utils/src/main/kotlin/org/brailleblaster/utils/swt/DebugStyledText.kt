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
package org.brailleblaster.utils.swt

import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.widgets.Composite
import org.slf4j.LoggerFactory

/**
 * Provide exception info when debugging StyledText issues
 */
class DebugStyledText(parent: Composite?, style: Int) : StyledText(parent, style) {

  private val logger = LoggerFactory.getLogger(DebugStyledText::class.java)

  override fun getLineAtOffset(offset: Int): Int {
    return try {
      super.getLineAtOffset(offset)
    } catch (e: IllegalArgumentException) {
      logger.error("Error whilst getting line for offset=${offset}", e)
      0
    }
  }

  override fun getLine(lineIndex: Int): String {
    return try {
      super.getLine(lineIndex)
    } catch (_: IllegalArgumentException) {
      logger.error("getLine cursor movement exception. lineIndex: $lineIndex not found. charCount: $charCount lines: $lineCount. returning empty string")
      ("")
    }
  }

  override fun getText(start: Int, end: Int): String {
    return try {
      super.getText(start, end)
    } catch (_: IllegalArgumentException) {
      logger.error("getText cursor movement exception. start: $start end: $end charCount: $charCount. returning empty string")
      ("")
    }
  }

  override fun getTextRange(start: Int, length: Int): String {
    return try {
      super.getTextRange(start, length)
    } catch (_: IllegalArgumentException) {
      logger.error("getTextRange cursor movement exception. start $start: length: $length charCount: $charCount. returning empty string")
      ("")
    }
  }

  override fun setSelection(start: Int, end: Int) {
    try {
      super.setSelection(start, end)
    } catch (_: IllegalArgumentException) {
      logger.error("setSelection cursor movement exception. start: $start end: $end charCount: $charCount")
    }
  }

  override fun setSelectionRange(start: Int, length: Int) {
    try {
      super.setSelectionRange(start, length)
    } catch (_: IllegalArgumentException) {
      logger.error("setSelectionRange cursor movement exception. start: $start length: $length charCount: $charCount")
    }
  }

  override fun setCaretOffset(offset: Int) {
    try {
      super.setCaretOffset(offset)
    } catch (_: IllegalArgumentException) {
      logger.error("setCaretOffset cursor movement exception. offset $offset failed. charCount: $charCount")
    }
  }
}