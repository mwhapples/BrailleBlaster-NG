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
package org.brailleblaster.settings.ui

import org.eclipse.swt.SWT

class Loadout(@JvmField var name: String, @JvmField var accelerator: Int) {
    companion object {
        @JvmField
		var list = ArrayList<Loadout>()
        @JvmStatic
		fun listLoadouts() {
            if (list.size == 0) {
                list.add(Loadout("Basic", SWT.MOD1 + SWT.MOD2 + 'B'.code))
                list.add(Loadout("Captions", SWT.MOD1 + SWT.MOD2 + 'C'.code))
                list.add(Loadout("Heading", SWT.MOD1 + SWT.MOD2 + 'H'.code))
                list.add(Loadout("List", SWT.MOD1 + SWT.MOD2 + 'L'.code))
                list.add(Loadout("Poetry", SWT.MOD1 + SWT.MOD2 + 'P'.code))
                list.add(Loadout("Plays", SWT.MOD1 + SWT.MOD2 + 'A'.code))
                list.add(Loadout("Glossary", SWT.MOD1 + SWT.MOD2 + 'G'.code))
                list.add(Loadout("Exercise", SWT.MOD1 + SWT.MOD2 + 'E'.code))
                list.add(Loadout("Index", SWT.MOD1 + SWT.MOD2 + 'I'.code))
                list.add(Loadout("Numeric", SWT.MOD1 + SWT.MOD2 + 'U'.code))
                list.add(Loadout("Notes", SWT.MOD1 + SWT.MOD2 + 'N'.code))
                list.add(Loadout("Miscellaneous", SWT.MOD1 + SWT.MOD2 + '0'.code))
            }
        }

        @JvmStatic
		fun getAcc(name: String?): Int = list.firstOrNull { it.name.equals(name, ignoreCase = true) }?.accelerator ?: -1

        fun getName(accelerator: Int): String? = list.firstOrNull { it.accelerator == accelerator }?.name
    }
}
