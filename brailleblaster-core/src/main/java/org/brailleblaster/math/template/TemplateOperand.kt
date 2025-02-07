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
package org.brailleblaster.math.template

import org.brailleblaster.math.spatial.MathText

class TemplateOperand private constructor(var whole: MathText, var numerator: MathText, var denominator: MathText) {

    class TemplateOperandBuilder {
        private var whole = MathText()
        private var numerator = MathText()
        private var denominator = MathText()
        fun whole(whole: MathText): TemplateOperandBuilder {
            this.whole = whole
            return this
        }

        fun numerator(numerator: MathText): TemplateOperandBuilder {
            this.numerator = numerator
            return this
        }

        fun denominator(denominator: MathText): TemplateOperandBuilder {
            this.denominator = denominator
            return this
        }

        fun build(): TemplateOperand {
            return TemplateOperand(whole, numerator, denominator)
        }
    }
}
