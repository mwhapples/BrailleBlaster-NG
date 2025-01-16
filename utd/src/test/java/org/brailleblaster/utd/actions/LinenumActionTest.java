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

import nu.xom.Attribute;
import nu.xom.Element;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class LinenumActionTest {
	@Test
	public void processNodeTest() {
		Element p = new Element("p");
		p.appendChild("Line 1.");
		
		Element span = new Element("span");
		span.addAttribute(new Attribute("class", "linenum"));
		span.appendChild("2");
		p.appendChild(span);
		p.appendChild("Line 2.");
		
		String original = "<p>Line 1.<span class=\"linenum\">2</span>Line 2.</p>";
		assertEquals(p.toXML(), original);
		
		LinenumAction action = new LinenumAction();
		action.applyTo(span, new UTDTranslationEngine());
		assertEquals(span.getAttributeCount(), 6);
	}
	
	@Test
	public void processNodeWithNumTest() {
		Element p = new Element("p");
		p.appendChild("Line 1.");
		
		Element span = new Element("span");
		span.addAttribute(new Attribute("class", "linenum"));
		span.addAttribute(new Attribute("linenum","#b"));
		span.appendChild("2");
		p.appendChild(span);
		p.appendChild("Line 2.");
		
		String original = "<p>Line 1.<span class=\"linenum\" linenum=\"#b\">2</span>Line 2.</p>";
		assertEquals(p.toXML(), original);
		
		LinenumAction action = new LinenumAction();
		action.applyTo(span, new UTDTranslationEngine());
		String result = "<span class=\"linenum\" linenum=\"#b\" type=\"prose\" translated=\"true\" space=\"0\" />";
		assertEquals(span.toXML(), result);
		assertEquals(span.getAttributeCount(), 5);
	}
}
