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
import java.util.Iterator;
import java.util.List;

import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.brailleblaster.utd.IActionMap;
import org.brailleblaster.utd.ITranslationEngine;
import org.brailleblaster.utd.TextSpan;
import org.brailleblaster.utd.properties.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GenericActionTest {
	@DataProvider(name="textNodeProvider")
	public Object[][] textNodeProvider() {
		return new Object[][] {
				{new Text("Some text")},
				{new Text("Some other text")}
		};
	}
	@Test(dataProvider="textNodeProvider")
	public void applyToTextNode(Text t) {
		ITranslationEngine translationEngine = mock(ITranslationEngine.class);
		GenericAction action = new GenericAction();
		
		List<TextSpan> result = action.applyTo(t, translationEngine);
		
		assertEquals(result.size(), 1);
		TextSpan spanResult = result.get(0);
		assertEquals(spanResult.getText(), t.getValue());
		assertEquals(spanResult.getNode(), t);
		assertEquals(spanResult.getContentType(), ContentType.StandardText);
		assertTrue(spanResult.getEmphasis().isEmpty());
	}
	@DataProvider(name="elementProvider")
	public Iterator<Object[]> elementProvider() {
		List<Object[]> dataList = new ArrayList<>();
		Element e = new Element("e");
		List<TextSpan> expectedList = new ArrayList<>();
		Node child = new Text("Some ");
		e.appendChild(child);
		TextSpan span = new TextSpan(child, child.getValue());
		expectedList.add(span);
		child = new Text("text ");
		e.appendChild(child);
		span = new TextSpan(child, child.getValue());
		expectedList.add(span);
		child = new Text("content");
		e.appendChild(child);
		span = new TextSpan(child, child.getValue());
		expectedList.add(span);
		
		dataList.add(new Object[] {e, expectedList});
		return dataList.iterator();
	}
	@Test(dataProvider="elementProvider")
	public void applyToElements(Element e, List<TextSpan> expectedList) {
		GenericAction action = new GenericAction();
		IActionMap mockActionMap = mock(IActionMap.class);
		when(mockActionMap.findValueOrDefault(any(Node.class))).thenReturn(action);
		ITranslationEngine mockTranslationEngine = mock(ITranslationEngine.class);
		when(mockTranslationEngine.getActionMap()).thenReturn(mockActionMap);
		
		List<TextSpan> result = action.applyTo(e, mockTranslationEngine);
		
		assertEquals(result.size(), expectedList.size());
		for (int i = 0; i < result.size(); i++) {
			TextSpan resultSpan = result.get(i);
			TextSpan expectedSpan = expectedList.get(i);
			assertEquals(resultSpan.getNode(), expectedSpan.getNode());
			assertEquals(resultSpan.getText(), expectedSpan.getText());
		}
	}
	@DataProvider(name="ignoredNodeTypesProvider")
	public Object[][] ignoredNodeTypesProvider() {
		return new Object[][] {
				{new Comment("A comment")},
				{new ProcessingInstruction("include", "SomeFile.txt")}
		};
	}
	@Test(dataProvider="ignoredNodeTypesProvider")
	public void applyToIgnoreNodes(Node node) {
		String expectedXML = node.toXML();
		ITranslationEngine engine = mock(ITranslationEngine.class);
		GenericAction action = new GenericAction();
		
		List<TextSpan> result = action.applyTo(node, engine);
		assertTrue(result.isEmpty());
		assertEquals(node.toXML(), expectedXML);
	}
	
	@Test
	public void saneEqualsTest() {
		assertEquals(new GenericAction(), new GenericAction());
		
		List<IAction> actions = new ArrayList<>();
		actions.add(new GenericAction());
		
		assertTrue(actions.contains(new GenericAction()));
	}
}
