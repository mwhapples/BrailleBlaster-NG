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
package org.brailleblaster.utd.internal.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import nu.xom.Node;
import nu.xom.ParentNode;
import org.brailleblaster.utd.exceptions.NodeException;
import org.brailleblaster.utils.xom.NodeUtilsKt;
import org.jetbrains.annotations.NotNull;

/**
 * Fast pure Java implementations of common XPath queries
 */
public class FastXPath {

	public static StreamableIterable<Node> descendantOrSelf(Node startNode) {
		return () -> new NodeIterator(startNode, true, true);
	}

	@SuppressWarnings("unchecked")
	public static <N extends Node> List<N> descendantFindList(Node startNode, BiPredicate<List<N>, Node> matcher) {
		ArrayList<N> results = new ArrayList<>();
		descendantFindFirst(startNode, (curNode) -> {
			if (matcher.test(results, curNode)) {
				results.add((N) curNode);
			}
			return true;
		});
		return results;
	}

	@SuppressWarnings("unchecked")
	public static <N extends Node> N descendantFindOnly(Node startNode, Predicate<Node> matcher) {
		if (startNode == null) {
			throw new NullPointerException("startNode cannot be null");
		} else if (matcher == null) {
			throw new NullPointerException("matcher cannot be null");
		}
		AtomicReference<Node> mutableObject = new AtomicReference<>();
		return (N) descendantFindFirst(startNode, (curNode) -> {
			if (matcher.test(curNode)) {
				if (mutableObject.get() != null) {
					Objects.requireNonNull(XMLHandler.nodeToElementOrParentOrDocRoot(startNode)).addAttribute(new Attribute("first", "match"));
					throw new NodeException("Already matched element with first=match attrib, matched again: ", startNode);
				}
				mutableObject.set(startNode);
			}
			return true;
		});
	}

	/**
	 * Keep processing until predicate returns false, then return stopped node
	 */
	public static Node descendantFindFirst(Node startNode, Predicate<Node> matcher) {
		if (!matcher.test(startNode)) {
			return startNode;
		}
		for (Node curNode : NodeUtilsKt.getChildNodes(startNode)) {
			Node subResult = descendantFindFirst(curNode, matcher);
			if (subResult != null) {
				return subResult;
			}
		}
		return null;
	}

	public static StreamableIterable<Node> descendant(Node startNode) {
		return () -> {
			Iterator<Node> result = descendantOrSelf(startNode).iterator();
			//Skip first node which is the start node
			result.next();
			return result;
		};
	}

	public static StreamableIterable<Node> following(Node startNode) {
		return () -> new NodeIterator(
				NodeIterator.itrNextNode(startNode, null, true),
				false,
				true
		);
	}

	public static StreamableIterable<Node> followingAndSelf(Node startNode) {
		return () -> new NodeIterator(
				startNode,
				false,
				true
		);
	}

	/**
	 * Iterate backwards through the xml. Note: This is different from XPath's preceding
	 * because it will match ancestors.
	 */
	@NotNull
	public static StreamableIterable<@NotNull Node> preceding(Node startNode) {
		return () -> new NodeIterator(
				NodeIterator.itrNextNode(startNode, null, false),
				false,
				false
		);
	}

	/**
	 * Iterate backwards through the xml, starting with the given node.
	 * Note: This is different from XPath's preceding because it will
	 * match ancestors.
	 */
	public static StreamableIterable<Node> precedingAndSelf(Node startNode) {
		return () -> new NodeIterator(startNode, false, false);
	}

	public static StreamableIterable<Node> descendantAndFollowing(Node startNode) {
		return () -> new NodeIterator(startNode, false, true);
	}

	@SuppressWarnings("unchecked")
	public static StreamableIterable<Element> ancestor(Node startNode) {
		return (StreamableIterable<Element>) (Object) _ancestor(startNode.getParent() instanceof Element
				? startNode.getParent()
				: null
		);
	}

	public static StreamableIterable<Node> ancestorOrSelf(Node startNode) {
		return _ancestor(startNode);
	}

	public static StreamableIterable<Node> _ancestor(Node actualStart) {
		return () -> new Iterator<>() {
			Node curElement = actualStart;

			@Override
			public boolean hasNext() {
				return curElement != null;
			}

			@Override
			public Node next() {
				Node next = curElement;
				ParentNode parent = curElement.getParent();
				if (parent instanceof Document) {
					//stopping
					curElement = null;
				} else {
					curElement = parent;
				}
				return next;
			}
		};
	}

}
