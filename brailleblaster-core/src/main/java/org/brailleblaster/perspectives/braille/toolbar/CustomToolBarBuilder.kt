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
package org.brailleblaster.perspectives.braille.toolbar

import org.brailleblaster.utils.swt.EasySWT
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.*
import java.util.function.Consumer
import java.util.function.Supplier

open class CustomToolBarBuilder {
    val widgets: MutableList<CustomWidget> = mutableListOf()

    open fun build(parent: Composite): Composite? {
        widgets.forEach(Consumer { w: CustomWidget -> w.build(parent) })
        return parent
    }

    @JvmOverloads
    fun addText(
        width: Int,
        defaultValue: String = "",
        swtBotId: String? = null,
        callback: Consumer<Text?>? = null
    ): CustomText {
        return CustomText(this, width, defaultValue, swtBotId, callback)
    }

    @JvmOverloads
    fun addLabel(text: String, width: Int, swtBotId: String? = null): CustomLabel {
        return CustomLabel(this, width, text, swtBotId)
    }

    @JvmOverloads
    fun addButton(
        text: String?,
        width: Int,
        onSelect: Consumer<SelectionEvent>,
        swtBotId: String? = null
    ): CustomButton {
        return CustomButton(this, width, text, onSelect, swtBotId)
    }

    fun addCheckBox(
        text: String,
        width: Int,
        onSelect: Consumer<SelectionEvent>,
        swtBotId: String,
        value: Supplier<Boolean>
    ): CustomCheckBox {
        return CustomCheckBox(this, width, text, onSelect, swtBotId, value)
    }

    abstract class CustomWidget(var parent: CustomToolBarBuilder, var width: Int, private val swtBotId: String?) {
        fun build(parent: Composite): Control {
            val control = doBuild(parent)
            if (swtBotId != null) {
                EasySWT.addSwtBotKey(control, swtBotId)
            }
            return control
        }

        abstract fun doBuild(parent: Composite?): Control
        abstract val widget: Control

        protected fun <T : Control?> checkWidget(widget: T?): T {
            if (widget == null) throw NullPointerException("Widget has not been created yet")
            return widget
        }
    }

    class CustomText(
        parent: CustomToolBarBuilder,
        width: Int,
        private val defaultValue: String,
        swtBotId: String?,
        private val callback: Consumer<Text?>?
    ) : CustomWidget(parent, width, swtBotId) {
        private var privWidget: Text? = null

        init {
            parent.widgets.add(this)
        }

        override fun doBuild(parent: Composite?): Control {
            val result = EasySWT.makeText(parent, width, 1)
            //TODO: This overrides makeText's GridData
            EasySWT.buildGridData().setAlign(SWT.DEFAULT, SWT.CENTER).setHint(width, SWT.DEFAULT).applyTo(result)
            result.text = defaultValue
            callback?.accept(result)
            privWidget = result
            return result
        }

        override val widget: Text
            get() = checkWidget(privWidget)
    }

    class CustomLabel(parent: CustomToolBarBuilder, width: Int, val text: String, swtBotId: String?) :
        CustomWidget(parent, width, swtBotId) {
        private var privWidget: Label? = null

        init {
            parent.widgets.add(this)
        }

        override fun doBuild(parent: Composite?): Control {
            val result = EasySWT.makeLabel(parent, text, 1)
            EasySWT.buildGridData().setAlign(SWT.DEFAULT, SWT.CENTER).setHint(width, SWT.DEFAULT).applyTo(result)
            privWidget = result
            return result
        }

        override val widget: Label
            get() = checkWidget(privWidget)
    }

    open class CustomButton(
        parent: CustomToolBarBuilder,
        width: Int,
        val text: String?,
        var onSelect: Consumer<SelectionEvent>,
        swtBotId: String?
    ) : CustomWidget(parent, width, swtBotId) {
        private var privWidget: Button? = null

        init {
            parent.widgets.add(this)
        }

        override fun doBuild(parent: Composite?): Control {
            val result = EasySWT.makePushButton(parent, text, 1, onSelect)
            EasySWT.buildGridData().setAlign(SWT.DEFAULT, SWT.CENTER).setHint(width, SWT.DEFAULT).applyTo(result)
            privWidget = result
            return result
        }

        override val widget: Button
            get() = checkWidget(privWidget)
    }

    class CustomCheckBox(
        parent: CustomToolBarBuilder,
        width: Int,
        val text: String,
        var onSelect: Consumer<SelectionEvent>,
        swtBotId: String,
        /**
         * Use in case of rebuilding
         */
        val value: Supplier<Boolean>
    ) : CustomWidget(parent, width, swtBotId) {
        private var privWidget: Button? = null

        init {
            parent.widgets.add(this)
        }

        override fun doBuild(parent: Composite?): Control {
            val result = EasySWT.makeCheckBox(parent, text, onSelect)
            EasySWT.buildGridData().setAlign(SWT.DEFAULT, SWT.CENTER).setHint(width, SWT.DEFAULT).applyTo(result)
            result.selection = value.get()
            privWidget = result
            return result
        }

        override val widget: Button
            get() = checkWidget(privWidget)
    }

    companion object {
        @JvmStatic
		fun userDefined(handler: Consumer<Composite>): CustomToolBarBuilder {
            return object : CustomToolBarBuilder() {
                override fun build(parent: Composite): Composite? {
                    handler.accept(parent)

                    //unused in implementation
                    return null
                }
            }
        }
    }
}

