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
 * An EventFrame contains a list of Modular and View events that represent each
 * edit made during one ModifyEvent (or Text View edit for ViewEvents)
 */
class EventFrame {
    private val eventList: MutableList<Event> = mutableListOf()
    var id = 0


    fun addEvent(event: Event) {
        eventList.add(event)
    }

    fun addEvent(index: Int, event: Event) {
        eventList.add(index, event)
    }

    fun size(): Int {
        return eventList.size
    }

    operator fun get(index: Int): Event {
        return eventList[index]
    }

    fun peek(): Event? {
        return eventList.lastOrNull()
    }

    fun pop(): Event? {
        return if (!empty()) eventList.removeAt(eventList.size - 1) else null
    }

    fun push(): Event? {
        return if (!empty()) eventList.removeAt(0) else null
    }

    fun empty(): Boolean {
        return eventList.isEmpty()
    }

    fun reverse() {
        eventList.reverse()
    }
}