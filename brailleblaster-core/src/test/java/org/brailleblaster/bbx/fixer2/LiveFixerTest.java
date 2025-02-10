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
package org.brailleblaster.bbx.fixer2;

import nu.xom.Element;
import org.brailleblaster.bbx.BBX;
import org.brailleblaster.bbx.fixers2.LiveFixer;
import org.brailleblaster.testrunners.BBXDocFactory;
import org.brailleblaster.testrunners.XMLElementAssert;
import org.brailleblaster.utd.properties.EmphasisType;
import org.testng.annotations.Test;

public class LiveFixerTest {
	@Test
	public void doc_blockWithSpaces_StartEndAndEmpty() {
		Element root = new BBXDocFactory()
				.append(BBX.BLOCK.DEFAULT.create(), "test1 ")
				.append(BBX.BLOCK.DEFAULT.create(), "  ")
				.append(BBX.BLOCK.DEFAULT.create(), " test2")
				.root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.hasText("test1 ")
				).nextChildIs(p -> p
						.hasText("  ")
				).nextChildIs(p -> p
						.hasText(" test2")
				).noNextChild();
		
		LiveFixer.fix(root);
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.hasText("test1")
				).nextChildIs(p -> p
						.hasText("test2")
				).noNextChild();
	}
	
	@Test
	public void doc_removeEmptyContainers() {
		Element root = new BBXDocFactory()
				.append(BBX.CONTAINER.LIST.create(BBX.ListType.NORMAL))
				// not valid but ok for this test
				.append(BBX.CONTAINER.IMAGE.create(), "test")
				.root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.childCount(0)
				).nextChildIs(p -> p
						.hasText("test")
				).noNextChild();
		
		LiveFixer.fix(root);
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.hasText("test")
				).noNextChild();
	}
	
	@Test
	public void doc_blockWithSpaces_outer() {
		Element root = new BBXDocFactory()
				.append(BBX.BLOCK.DEFAULT.create(), children -> children
						.append(" ")
						.append(BBX.INLINE.EMPHASIS.create(EmphasisType.BOLD), "bold")
						.append(" ")
				).root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIsText(" ")
						.nextChildIs(bold -> bold
								.hasText("bold")
						).nextChildIsText(" ")
				).noNextChild();
		
		LiveFixer.fix(root);
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).noNextChild()
				).noNextChild();
	}
	
	@Test
	public void doc_blockWithSpaces_nestedAndOuter() {
		Element root = new BBXDocFactory()
				.append(BBX.BLOCK.DEFAULT.create(), children -> children
						.append(" ")
						.append(BBX.INLINE.EMPHASIS.create(EmphasisType.BOLD), "  bold  ")
						.append(" ")
				).root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIsText(" ")
						.nextChildIs(bold -> bold
								.hasText("  bold  ")
						).nextChildIsText(" ")
				).noNextChild();
		
		LiveFixer.fix(root);
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).noNextChild()
				).noNextChild();
	}
	
	@Test
	public void doc_blockWithSpaces_afterNoSpaceText() {
		Element root = new BBXDocFactory()
				.append(BBX.BLOCK.DEFAULT.create(), children -> children
						.append(BBX.INLINE.EMPHASIS.create(EmphasisType.BOLD), "bold")
						.append(" test")
				).root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).nextChildIsText(" test")
						.noNextChild()
				).noNextChild();
		
		LiveFixer.fix(root);
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).nextChildIsText(" test")
				).noNextChild();
	}
	
	@Test
	public void doc_emptyTextNode() {
		Element root = new BBXDocFactory()
				.append(BBX.BLOCK.DEFAULT.create(), children -> children
						.append(BBX.INLINE.EMPHASIS.create(EmphasisType.BOLD), "bold")
						.append("")
						.append(BBX.INLINE.EMPHASIS.create(EmphasisType.ITALICS), "italics")
				).root;
		
		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).nextChildIsText("")
						.nextChildIs(italics -> italics
								.hasText("italics")
						).noNextChild()
				).noNextChild();
		
		LiveFixer.fix(root);

		new XMLElementAssert(root, null)
				.nextChildIs(p -> p
						.nextChildIs(bold -> bold
								.hasText("bold")
						).nextChildIs(italics -> italics
								.hasText("italics")
						).noNextChild()
				).noNextChild();
	}
}
