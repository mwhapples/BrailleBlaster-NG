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

import java.util.EnumSet;

import nu.xom.Node;

import org.brailleblaster.utd.properties.EmphasisType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BoldActionTest extends BaseEmphasisActionTest {
	@Test(dataProvider="nodesProvider", dataProviderClass=BaseEmphasisActionTest.class)
	public void applyTo(Node node) {
		super.testApplyTo(node, new BoldAction(), EnumSet.of(EmphasisType.BOLD));
	}
	
	@Test
	public void equalsTest() {
		Assert.assertEquals(new BoldAction(), new BoldAction());
	}
}
