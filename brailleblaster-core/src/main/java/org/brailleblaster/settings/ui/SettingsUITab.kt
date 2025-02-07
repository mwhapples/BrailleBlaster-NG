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

import org.brailleblaster.utd.UTDTranslationEngine

interface SettingsUITab {
    /**
     * Validate tab data
     * @return NULL if no errors, i18n key of error message if there is an error
     */
    fun validate(): String?

    /**
     * When all other tab validation has passed, update the engine
     * @param engine
     * @return True if values were changed and need to be saved, false if not
     */
    fun updateEngine(engine: UTDTranslationEngine): Boolean
}
