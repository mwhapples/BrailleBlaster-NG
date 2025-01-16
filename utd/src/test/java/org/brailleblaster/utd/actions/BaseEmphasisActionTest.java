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
package org.brailleblaster.utd.actions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.brailleblaster.utd.ITranslationEngine;
import org.brailleblaster.utd.TextSpan;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.brailleblaster.utd.properties.EmphasisType;
import org.testng.annotations.DataProvider;

import static org.testng.Assert.assertTrue;

public abstract class BaseEmphasisActionTest {
	@DataProvider(name="nodesProvider")
	public static Iterator<Object[]> nodesProvider() {
		List<Object[]> dataList = new ArrayList<>();
		Node node = new Text("Some text");
		dataList.add(new Object[] {node});
		Element root = new Element("p");
		root.appendChild("Some text");
		root.appendChild(new Element("img"));
		dataList.add(new Object[] {root});
		return dataList.iterator();
	}
	protected void testApplyTo(Node node, IAction action, EnumSet<EmphasisType> emphasis) {
		ITranslationEngine contextMock = new UTDTranslationEngine();
		List<TextSpan> result = action.applyTo(node, contextMock);
		
		for (TextSpan input: result) {
			assertTrue(input.getEmphasis().containsAll(emphasis));
		}
	}
}
