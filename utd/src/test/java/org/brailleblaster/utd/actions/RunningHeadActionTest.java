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
import org.brailleblaster.utd.ITranslationEngine;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.testng.annotations.Test;

public class RunningHeadActionTest {	
	@Test
	public void applyTo() {
		ITranslationEngine engine = new UTDTranslationEngine();
		Element root = new Element("root");
		Element doctitle = new Element("doctitle");
		doctitle.appendChild("text");
		root.appendChild(doctitle);

		RunningHeadAction action = new RunningHeadAction();
//		List<TextSpan> result = 
				action.applyTo(doctitle, engine);
//		assertTrue(result.isEmpty());
//		TextTranslator translator = new TextTranslator();
//		String title = translator.translateText(doctitle.getValue(), engine);
//		assertEquals(engine.getPageSettings().getRunningHead(), title);
		
	}
}
