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
package org.brailleblaster.utd;

import nu.xom.Element;
import org.brailleblaster.TestGroups;
import org.brailleblaster.testrunners.BBTestRunner;
import org.brailleblaster.testrunners.TestXMLUtils;
import org.brailleblaster.testrunners.ViewTestRunner;
import org.brailleblaster.utd.actions.PageAction;
import org.eclipse.swt.SWT;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.testng.Assert.assertEquals;

public class BBTest {

    @Test(enabled = false)
    public void issue3602() {
        BBTestRunner bbTest = new BBTestRunner("", "<p>This is a test</p><p>Another paragraph</p>");

        bbTest.navigateTextView(0);

        bbTest.textViewTools.selectToEndOfLine();
        assertEquals(bbTest.textViewBot.getSelection(), "This is a test");

        bbTest.textViewBot.contextMenu("Hide").click();
        ViewTestRunner.doPendingSWTWork();

        assertEquals(bbTest.textViewBot.getText().trim(), "Another paragraph");
    }

    @Test(enabled = false)
    public void keepApplyingStyles() {
        BBTestRunner bbTest = new BBTestRunner("", "<p testid='p1'>This is a test</p><p>Another paragraph</p>");

        bbTest.textViewTools.navigateToLine(2);

        bbTest.textViewTools.selectLeft(1);
        ViewTestRunner.doPendingSWTWork();

        //Note: Don't use Keystrokes.LF/CR due to linux/windows compatability
        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.pressShortcut(SWT.CTRL, 'z');
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.pressShortcut(SWT.CTRL, 'z');
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.pressShortcut(SWT.CTRL, 'y');
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.typeText("test");

        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.pressShortcut(SWT.CTRL, 'z');
        ViewTestRunner.doPendingSWTWork();

        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();
    }

    @Test(groups = TestGroups.BROKEN_TESTS) //No idea what this is testing, hg blame mentiones cdata
    //but it's splitting <h1><p>Overly nested elements</p></h1> ?
    public void splitHeadingTest() {
        BBTestRunner bbTest = new BBTestRunner(new File("src/test/resources/org/brailleblaster/easierxml/nimas-test.xml"));

        bbTest.textViewTools.navigateToText("nested");
        bbTest.textViewBot.typeText(System.lineSeparator());
        ViewTestRunner.doPendingSWTWork();
    }

    @Test(groups = TestGroups.TODO_FIX_LATER) // TODO: Why does this still fail...
    public void issue3650() {
        BBTestRunner bbTest = new BBTestRunner("", "<p><strong>K</strong>now</p>");
        assertThat(bbTest.brailleViewWidget.getText(), startsWith("  ~2,\"k"));
        assertThat(bbTest.textViewWidget.getText(), startsWith("  Know"));
    }

    @Test(groups = TestGroups.TODO_FIX_LATER) //TODO: Open doesn't work anymore under debug in Modules
    public void issue3712() {
        BBTestRunner bbTest = new BBTestRunner("", "<p>test</p>");

        bbTest.bot.menu("Close").click();
        ViewTestRunner.doPendingSWTWork();

        bbTest.bot.menu("Open").click();
        ViewTestRunner.doPendingSWTWork();
    }

    @Test(enabled = false)
    public void pageTest() {
        BBTestRunner bbTest = new BBTestRunner("", "<p>test</p><pagenum testid='page'>4</pagenum><p>after</p>");
        Element pageNum = TestXMLUtils.getTestIdElement(bbTest.getDoc(), "page");
        IActionMap actionMap = bbTest.manager.getDocument().getEngine().getActionMap();
        assertThat(actionMap.findValueOrDefault(pageNum), instanceOf(PageAction.class));
    }
}
