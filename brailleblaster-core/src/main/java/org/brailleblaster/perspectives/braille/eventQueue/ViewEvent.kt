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
package org.brailleblaster.perspectives.braille.eventQueue

/**
 * A ViewEvent is an undo event that occurs when text has been typed but the
 * translation has yet to be updated. These are deleted from the queue when
 * a ModularEvent is added
 */
class ViewEvent(
    eventType: EventTypes,
    textStart: Int,
    val textEnd: Int,
    val brailleOffset: Int,
    val brailleEnd: Int,
    val text: String
) : Event(eventType, textStart)