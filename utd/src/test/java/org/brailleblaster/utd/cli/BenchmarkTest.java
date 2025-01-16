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
package org.brailleblaster.utd.cli;

import java.util.ArrayList;
import java.util.List;
import org.brailleblaster.utd.PageSettings;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BenchmarkTest {
	@DataProvider
	public Object[][] pageSizeDataProvider() {
		List<Object[]> result = new ArrayList<>();
		for (int i = 1; i < 50; i++) {
			result.add(new Object[]{i});
		}
		return result.toArray(new Object[result.size()][]);
	}
	
	@Test(dataProvider = "pageSizeDataProvider")
	public void setNewPageHeightTest(int size) {
		PageSettings pageSettings = new PageSettings();
		Benchmark.setNewPageHeight(pageSettings, BrlCell.NLS, size);
	}
	
	@Test(dataProvider = "pageSizeDataProvider")
	public void setNewPageWidthTest(int size) {
		PageSettings pageSettings = new PageSettings();
		Benchmark.setNewPageWidth(pageSettings, BrlCell.NLS, size);
	}
}
