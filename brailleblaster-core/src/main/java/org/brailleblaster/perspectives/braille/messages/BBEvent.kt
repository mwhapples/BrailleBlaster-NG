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
package org.brailleblaster.perspectives.braille.messages

//Enumeration used by Message class used by the DocumentManager to redirect messages between views
enum class BBEvent {
    INCREMENT, DECREMENT, WHITESPACE_TRANSFORM, WHITESPACE_DELETION, EDIT, SELECTION, UPDATE, MERGE, INSERT_NODE, REMOVE_NODE, SET_CURRENT, GET_CURRENT, GET_TEXT_MAP_ELEMENTS, ADJUST_LOCAL_STYLE, UPDATE_STATUSBAR, UPDATE_SCROLLBAR, UPDATE_STYLE, ADJUST_RANGE, SPLIT_TREE, TAB_INSERTION, TAB_ADJUSTMENT, TAB_DELETION
}