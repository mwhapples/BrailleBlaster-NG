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

import nu.xom.Element;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MoveSiblingActionTest {
	@Test
	public void processNodeTest() {
		Element lineGrp = new Element("linegroup");
		Element line1 = new Element("line");
		Element line2 = new Element("line");
		Element code = new Element("code");
		code.appendChild("1");
		Element line3 = new Element("line");
		Element line4 = new Element("line");
		
		lineGrp.appendChild(line1);
		lineGrp.appendChild(line2);
		lineGrp.appendChild(code);
		lineGrp.appendChild(line3);
		lineGrp.appendChild(line4);
		
		String original = "<linegroup><line /><line /><code>1</code><line /><line /></linegroup>";
		assertEquals(lineGrp.toXML(), original);
		MoveSiblingAction action = new MoveSiblingAction();
		action.applyTo(code, new UTDTranslationEngine());
		
		String result = "<linegroup linenum=\"⠼⠁\"><line /><line /><line /><code>1</code><line /></linegroup>";
		assertEquals(lineGrp.toXML(), result);
	}
	
	@Test
	public void processLastLineNumNodeTest() {
		Element lineGrp = new Element("linegroup");
		Element line1 = new Element("line");
		Element line2 = new Element("line");
		Element code = new Element("code");
		code.appendChild("1");
		Element line3 = new Element("line");
		Element line4 = new Element("line");
		
		lineGrp.appendChild(line1);
		lineGrp.appendChild(line2);
		lineGrp.appendChild(line3);
		lineGrp.appendChild(line4);
		lineGrp.appendChild(code);
		
		String original = "<linegroup><line /><line /><line /><line /><code>1</code></linegroup>";
		assertEquals(lineGrp.toXML(), original);
		MoveSiblingAction action = new MoveSiblingAction();
		action.applyTo(code, new UTDTranslationEngine());
		
		String result = "<linegroup linenum=\"⠼⠁\"><line /><line /><line /><line /><code>1</code></linegroup>";
		assertEquals(lineGrp.toXML(), result);
	}
	
	@Test
	public void processLastLineLetterNodeTest() {
		Element lineGrp = new Element("linegroup");
		Element line1 = new Element("line");
		Element line2 = new Element("line");
		Element code = new Element("code");
		code.appendChild("a");
		Element line3 = new Element("line");
		Element line4 = new Element("line");
		
		lineGrp.appendChild(line1);
		lineGrp.appendChild(line2);
		lineGrp.appendChild(line3);
		lineGrp.appendChild(line4);
		lineGrp.appendChild(code);
		
		String original = "<linegroup><line /><line /><line /><line /><code>a</code></linegroup>";
		assertEquals(lineGrp.toXML(), original);
		MoveSiblingAction action = new MoveSiblingAction();
		action.applyTo(code, new UTDTranslationEngine());
		
		String result = "<linegroup lineletter=\"⠁\"><line /><line /><line /><line /><code>a</code></linegroup>";
		assertEquals(lineGrp.toXML(), result);
	}
	
	@Test
	public void processTextNodeTest() {
		Element p = new Element("p");
		Element code = new Element("code");
		code.appendChild("5");
		
		p.appendChild(code);
		p.appendChild("Some text");
		
		String original = "<p><code>5</code>Some text</p>";
		assertEquals(p.toXML(), original);
		MoveSiblingAction action = new MoveSiblingAction();
		action.applyTo(code, new UTDTranslationEngine());
		
		//Make sure there are no brls
		assertEquals(p.getChild(2).getChildCount(), 1);
	}
	
	@Test
	public void processOnlyNodeTest() {
		Element lineGroup = new Element("linegroup");
		Element line = new Element("line");
		Element code = new Element("code");
		Element line2 = new Element("line");
		line2.appendChild("Some text");
		
		code.appendChild("5");
		line.appendChild(code);
		
		lineGroup.appendChild(line);
		lineGroup.appendChild(line2);
		
		MoveSiblingAction action = new MoveSiblingAction();
		action.applyTo(code, new UTDTranslationEngine());
		
		assertEquals(lineGroup.getChildCount(), 1);
	}
}
