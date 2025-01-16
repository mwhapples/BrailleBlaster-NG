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
package org.brailleblaster.utd.actions

import nu.xom.Node
import org.brailleblaster.utd.internal.InterfaceAdapter
import org.brailleblaster.utd.ITranslationEngine
import org.brailleblaster.utd.TextSpan
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * Interface for action objects.
 *
 *
 * Implement this interface should you need to provide a new action implementation. It is
 * recommended that you check that none of the current action implementations cannot be customised
 * to meet your needs before creating a new implementation.
 *
 *
 * To keep action implementations independent of the persistence format for configurations, it is
 * recommended that implementations follow the java beans conventions for anything which needs to be
 * persisted. Should it not be appropriate for a implementation to have a public no-args
 * constructor, it is acceptable for the no-args constructor to be private.
 *
 *
 * Should a implementation need greater control over storage, the current default persistence
 * implementation is JAXB, so JAXB annotations may be used to customise the persistence. However it
 * is not guaranteed that the default persistence implementation will be used on all systems and the
 * default may change over time.
 */
@XmlJavaTypeAdapter(InterfaceAdapter::class)
interface IAction {
    /**
     * Apply the action to the node and obtain the a list of TranslationInput for translation.
     *
     *
     * This method is used to process the node, creating brl nodes for the content and associating
     * brl nodes for any of its children. This method should not associate the brl node it returns
     * with the node which is passed in, that is the responsibility of the caller.
     *
     * @param node The node the semantic action is being applied to.
     * @param context The configuration which should be used for processing child nodes.
     * @return The brl node containing the translation for the node passed in. If no brl node is
     * generated then null will be returned. The brl node is not added to the document, that will
     * need to be done by the caller.
     */
    fun applyTo(node: Node, context: ITranslationEngine): List<TextSpan>
}

interface IBlockAction : IAction { // public List<TranslationInput> applyBlock(Node node, ITranslationContext context);
}