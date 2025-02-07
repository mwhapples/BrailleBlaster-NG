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
package org.brailleblaster.perspectives.braille.views.tree

import nu.xom.Element
import nu.xom.Text
import org.brailleblaster.BBIni
import org.brailleblaster.abstractClasses.AbstractView
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.braille.mapping.elements.BoxLineTextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.ImagePlaceholderTextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement
import org.brailleblaster.perspectives.braille.mapping.elements.WhiteSpaceElement
import org.brailleblaster.perspectives.mvc.XMLNodeCaret
import org.brailleblaster.perspectives.mvc.XMLTextCaret
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Tree
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

abstract class TreeView(manager: Manager, parent: Composite) : AbstractView(manager, parent), BBTree {
    final override val tree: Tree

    init {
        this.parent = parent
        tree = Tree(parent, SWT.VIRTUAL or SWT.BORDER)
    }

    protected fun determineEvent(t: TextMapElement, pos: Int): XMLNodeCaret {
        return when (t) {
            is BoxLineTextMapElement -> XMLNodeCaret(t.nodeParent)
            is ImagePlaceholderTextMapElement -> XMLNodeCaret(t.node)
            is WhiteSpaceElement -> {
                determineEvent(manager.mapList.getClosest(pos, true), 0)
            }

            else -> XMLTextCaret((t.node as Text), pos)
        }
    }

    override fun setViewData() {
        // TODO Auto-generated method stub
    }

    override fun dispose() {
        tree.removeAll()
        tree.dispose()
    }

    protected fun findPageNode(e: Element): Text? {
        if (e.childCount > 1 && e.getChild(1) is Element) {
            val brlNode = e.getChild(1) as Element
            if ((e.getChild(1) as Element).localName == "brl" && brlNode.childCount > 0) if (brlNode.getChild(0) is Element) {
                val spanNode = brlNode.getChild(0) as Element
                if (spanNode.localName == "span" && spanNode.getChild(0) is Text) {
                    return spanNode.getChild(0) as Text
                }
            }
        }
        return null
    }

    override val itemPath: ArrayList<Int>
        get() {
            val list = ArrayList<Int>()
            if (tree.selection.isNotEmpty()) {
                var item = tree.selection[0]
                val first = 0
                while (item.parentItem != null) {
                    list.add(first, item.parentItem.indexOf(item))
                    item = item.parentItem
                }
            }
            return list
        }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TreeView::class.java)
        fun loadTree(m: Manager?, sash: SashForm?): BBTree? {
            val prop = BBIni.propertyFileManager
            when (val tree = prop.getProperty("tree")) {
                null -> {
                    prop.save("tree", BookTree::class.java.canonicalName)
                    return BookTree(m, sash)
                }
                "empty" -> {
                    return null
                }
                else -> {
                    try {
                        val clss = Class.forName(tree)
                        return createTree(clss, m, sash)
                    } catch (e: ClassNotFoundException) {
                        logger.error("Class Not Found Exception", e)
                    }
                }
            }

            return null
        }

        fun createTree(clss: Class<*>, manager: Manager?, sashform: SashForm?): BBTree? {
            try {
                val constructor = clss.getConstructor(Manager::class.java, SashForm::class.java)
                return constructor.newInstance(manager, sashform) as BBTree
            } catch (e: NoSuchMethodException) {
                logger.error("No Such Method Exception", e)
            } catch (e: SecurityException) {
                logger.error("Security Exception", e)
            } catch (e: InstantiationException) {
                logger.error("Instantiation Exception", e)
            } catch (e: IllegalAccessException) {
                logger.error("Illegal Access Exception", e)
            } catch (e: IllegalArgumentException) {
                logger.error("Illegal Argument Exception", e)
            } catch (e: InvocationTargetException) {
                logger.error("Invocation Exception", e)
            }

            return null
        }
    }
}
