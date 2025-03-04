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
package org.brailleblaster.utd.internal

import org.apache.commons.collections4.MapIterator
import org.brailleblaster.utd.ActionMap
import org.brailleblaster.utd.actions.IAction
import org.brailleblaster.utd.matchers.INodeMatcher
import jakarta.xml.bind.annotation.adapters.XmlAdapter

class ActionMapAdapter : XmlAdapter<AdaptedActionMap?, ActionMap?>() {
    override fun marshal(actions: ActionMap?): AdaptedActionMap? {
        if (actions == null) {
            return null
        }
        val actionList = AdaptedActionMap()
        val defaultNamespaces = actions.namespaces
        val it: MapIterator<INodeMatcher, IAction> = actions.mapIterator()
        while (it.hasNext()) {
            val matcher = it.next()
            val action = it.value
            actionList.semanticEntries.add(AdaptedActionMap.Entry(matcher, action))
        }
        actionList.namespaces = defaultNamespaces
        return actionList
    }

    override fun unmarshal(actionList: AdaptedActionMap?): ActionMap? {
        if (actionList == null) {
            return null
        }
        val actionMap = ActionMap()
        actionMap.namespaces = actionList.namespaces
        for ((i, entry) in actionList.semanticEntries.withIndex()) {
            if (actionMap.containsKey(entry.matcher)) {
                // This will cause a cryptic "IndexOutOfBoundsException: Index: 2, Size: 1"
                // Give more information so someone knows what to fix
                throw RuntimeException(
                    "Detected duplicate matcher " + entry.matcher + " entry " + entry.action
                )
            }
            actionMap.put(i, entry.matcher, entry.action)
        }
        return actionMap
    }
}