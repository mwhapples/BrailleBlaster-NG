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
package org.brailleblaster.frontmatter;

import java.io.File;

import org.brailleblaster.perspectives.mvc.menu.TopMenu;
import org.brailleblaster.testrunners.BBTestRunner;
import org.brailleblaster.testrunners.ViewTestRunner;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.testng.annotations.Test;

public class TPageTest {
	@Test(enabled = false)
	public void emptyDocument_rt4646() {
		BBTestRunner bbTest = new BBTestRunner(new File("dist/programData/xmlTemplates/bbxTemplate.bbx"));
		
		bbTest.openMenuItem(TopMenu.TOOLS, "T-Page Generator");
		
		SWTBot bot = bbTest.bot.activeShell().bot();
		bot.styledText(0).typeText("this is a test");
		ViewTestRunner.doPendingSWTWork();
		bot.buttonWithId(TPagesDialog.SWTBOT_OK_BUTTON).click();
		ViewTestRunner.doPendingSWTWork();
	}
}
