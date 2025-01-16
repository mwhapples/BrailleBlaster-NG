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
package org.brailleblaster.utd.internal;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AdaptedNamespaceMapTest {
	@Test
	public void defaultConstructor() {
		List<NamespaceDefinition> nsList = new ArrayList<>();
		nsList.add(new NamespaceDefinition("xhtml", "http://www.w3.org/1999/xhtml"));
		nsList.add(new NamespaceDefinition("m", "http://www.w3.org/1998/Math/MathML"));
		AdaptedNamespaceMap nsMap = new AdaptedNamespaceMap(nsList);
		assertEquals(nsMap.getNamespaces(), nsList);
	}
}
