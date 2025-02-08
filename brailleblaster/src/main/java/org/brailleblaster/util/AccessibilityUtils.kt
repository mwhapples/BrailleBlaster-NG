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
package org.brailleblaster.util

import org.eclipse.swt.accessibility.ACC
import org.eclipse.swt.accessibility.AccessibleAdapter
import org.eclipse.swt.accessibility.AccessibleEvent
import org.eclipse.swt.widgets.Control

object AccessibilityUtils {
    /**
     * Set the name to be read when an accessibility device requests the name of a control (usually
     * when the control gains focus)
     */
    @JvmStatic
    fun setName(control: Control, name: String?) {
        checkArguments(control, name)
        control
            .accessible
            .addAccessibleListener(
                object : AccessibleAdapter() {
                    override fun getName(e: AccessibleEvent) {
                        e.result = name
                    }
                })
    }

    /**
     * Set text to be read before the existing name of a control when an accessibility device requests
     * the name of a control. Best used when a control already has an accessible name (like a button
     * with text) but more information is needed for accessibility purposes
     */
    fun prependName(control: Control, name: String) {
        checkArguments(control, name)
        control
            .accessible
            .addAccessibleListener(
                object : AccessibleAdapter() {
                    override fun getName(e: AccessibleEvent) {
                        e.result = if (e.result == null) name else name + " " + e.result
                    }
                })
    }

    @JvmStatic
    fun appendName(control: Control, name: String) {
        checkArguments(control, name)
        control
            .accessible
            .addAccessibleListener(
                object : AccessibleAdapter() {
                    override fun getName(e: AccessibleEvent) {
                        e.result = if (e.result == null) name else e.result + " " + name
                    }
                })
    }

    private fun checkArguments(control: Control?, name: String?) {
        requireNotNull(control) { "Control cannot be null" }
        requireNotNull(name) { "Name cannot be null" }
    }

    fun addLabelRelation(control: Control, label: Control) {
        val accCtrl = control.accessible
        val accLabel = label.accessible
        accLabel.addRelation(ACC.RELATION_LABEL_FOR, accCtrl)
        accCtrl.addRelation(ACC.RELATION_LABELLED_BY, accLabel)
    }

    fun addMemberRelation(control: Control, group: Control) {
        val accCtrl = control.accessible
        val accGroup = group.accessible
        accCtrl.addRelation(ACC.RELATION_MEMBER_OF, accGroup)
        accCtrl.addRelation(ACC.RELATION_LABELLED_BY, accGroup)
    }
}