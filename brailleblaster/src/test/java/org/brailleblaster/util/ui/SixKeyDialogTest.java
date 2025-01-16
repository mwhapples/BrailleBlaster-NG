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
package org.brailleblaster.util.ui;

import static org.hamcrest.Matchers.containsString;
import static org.testng.Assert.assertEquals;

import org.brailleblaster.testrunners.BBTestRunner;
import org.brailleblaster.testrunners.TestXMLUtils;
import org.brailleblaster.testrunners.ViewTestRunner;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

import nu.xom.Document;

public class SixKeyDialogTest {

    private static final Document RT6118 = TestXMLUtils.generateBookDoc("",
            "<p>" + "Copyright © 2015 Pearson Education, Inc., or its affiliates. All Rights Reserved. Printed in the United States of America. This publication is protected by copyright, and permission should be obtained from the publisher prior to any prohibited reproduction, storage in a retrieval system, or transmission in any form or by any means, electronic, mechanical, photocopying, recording, or likewise. For information regarding permissions, write to Rights Management Contracts, Pearson Education, Inc., One Lake Street, Upper Saddle River, New Jersey 07458."
                    + "</p>");

    @Test(enabled = false)
    public void rt_6118_incorrect_insertion_position() throws ParseException {
        /*
         * setup
         */
        BBTestRunner bbTest = new BBTestRunner(RT6118);
        bbTest.textViewTools.navigateToText("This publication");
        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();
        ViewTestRunner.forceActiveShellHack();
        SWTBotShell sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        SWTBot sixKeyBot = sixKeyShell.bot();
        SWTBotStyledText sixKeyText = sixKeyBot.styledText(0);

        /*
         * part 1 insert dot2, dot2, dot2 in front of "This publication"
         */
        sixKeyText.pressShortcut(KeyStroke.getInstance("D"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert Inline").click();
        ViewTestRunner.doPendingSWTWork();
        String s1 = bbTest.textViewBot.getTextOnLine(1).trim();
        String s2 = bbTest.textViewBot.getTextOnLine(2).trim();
        String s3 = bbTest.textViewBot.getTextOnLine(3).trim();
        String s4 = bbTest.textViewBot.getTextOnLine(4).trim();
        Assert.assertEquals(s1, "Inc., or its affiliates. All Rights");
        Assert.assertEquals(s2, "Reserved. Printed in the United States of");
        Assert.assertEquals(s3, "America. 1This publication is protected");
        Assert.assertEquals(s4, "by copyright, and permission should be obtained from the");
        /*
         * part 2 insert dot1, dot1, dot1 in front of the dot2, dot2, dot2 after
         * navigating to "the publisher prior"
         */
        bbTest.textViewTools.navigate(0);
        bbTest.textViewTools.navigateToText("1This");
        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();
        ViewTestRunner.forceActiveShellHack();
        sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        sixKeyBot = sixKeyShell.bot();
        sixKeyText = sixKeyBot.styledText(0);
        sixKeyText.pressShortcut(KeyStroke.getInstance("F"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert Inline").click();
        ViewTestRunner.doPendingSWTWork();
        s1 = bbTest.textViewBot.getTextOnLine(1).trim();
        s2 = bbTest.textViewBot.getTextOnLine(2).trim();
        s3 = bbTest.textViewBot.getTextOnLine(3).trim();
        s4 = bbTest.textViewBot.getTextOnLine(4).trim();
        Assert.assertEquals(s1, "Inc., or its affiliates. All Rights");
        Assert.assertEquals(s2, "Reserved. Printed in the United States of");
        Assert.assertEquals(s3, "America. a1This publication is protected by");
        Assert.assertEquals(s4, "copyright, and permission should be obtained from the");
        /*
         * part 3 insert dot5, dot5, dot5 in front of the 111
         */
        bbTest.textViewTools.navigate(0);
        bbTest.textViewTools.navigateToText("1This");
        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();
        ViewTestRunner.forceActiveShellHack();
        sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        sixKeyBot = sixKeyShell.bot();
        sixKeyText = sixKeyBot.styledText(0);
        sixKeyText.pressShortcut(KeyStroke.getInstance("J"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert Inline").click();
        ViewTestRunner.doPendingSWTWork();
        s1 = bbTest.textViewBot.getTextOnLine(1).trim();
        s2 = bbTest.textViewBot.getTextOnLine(2).trim();
        s3 = bbTest.textViewBot.getTextOnLine(3).trim();
        s4 = bbTest.textViewBot.getTextOnLine(4).trim();
        Assert.assertEquals(s1, "Inc., or its affiliates. All Rights");
        Assert.assertEquals(s2, "Reserved. Printed in the United States of");
        Assert.assertEquals(s3, "America. a`1This publication is protected");
        Assert.assertEquals(s4, "by copyright, and permission should be obtained from the");
    }

    @Test(enabled = false)
    public void rt_5999_not_translating_element_after_six_key() throws ParseException {
        BBTestRunner bbTest = new BBTestRunner("", "<p>There's no place like home, there's no place"
                + " like home, there's no place like home.  " + "Lions and tigers and bears oh my!</p>");

        bbTest.textViewTools.navigateToText("Lions");

        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();

        ViewTestRunner.forceActiveShellHack();
        SWTBotShell sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        SWTBot sixKeyBot = sixKeyShell.bot();
        SWTBotStyledText sixKeyText = sixKeyBot.styledText(0);

        sixKeyText.pressShortcut(KeyStroke.getInstance("F"), KeyStroke.getInstance("J"));
        sixKeyText.pressShortcut(KeyStroke.getInstance("D"), KeyStroke.getInstance("K"));
        sixKeyText.typeText(System.lineSeparator());
        sixKeyText.pressShortcut(KeyStroke.getInstance("F"), KeyStroke.getInstance("J"));
        sixKeyText.pressShortcut(KeyStroke.getInstance("D"), KeyStroke.getInstance("K"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert as Block").click();
        ViewTestRunner.doPendingSWTWork();

        String s1 = bbTest.textViewBot.getTextOnLine(1).trim();
        String s2 = bbTest.textViewBot.getTextOnLine(2).trim();
        String s3 = bbTest.textViewBot.getTextOnLine(3).trim();
        String s4 = bbTest.textViewBot.getTextOnLine(4).trim();
        Assert.assertEquals(s1, "home, there's no place like home.");
        Assert.assertEquals(s2, "c3");
        Assert.assertEquals(s3, "c3");
        Assert.assertEquals(s4, "Lions and tigers and bears oh my!");
    }

    /*
     * RT 4639
     */
    @Test(enabled = false)
    public void blankDocTest() throws ParseException {
        BBTestRunner bbTest = new BBTestRunner("", "<p></p>");

        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();

        ViewTestRunner.forceActiveShellHack();
        SWTBotShell sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        SWTBot sixKeyBot = sixKeyShell.bot();
        SWTBotStyledText sixKeyText = sixKeyBot.styledText(0);

        sixKeyText.pressShortcut(KeyStroke.getInstance("F"), KeyStroke.getInstance("J"));
        sixKeyText.pressShortcut(KeyStroke.getInstance("D"), KeyStroke.getInstance("K"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert Inline").click();
        ViewTestRunner.doPendingSWTWork();

        MatcherAssert.assertThat(bbTest.textViewWidget.getText(), containsString("c3"));
    }

    @Test(enabled = false)
    public void simpleTest() throws ParseException {
        BBTestRunner bbTest = new BBTestRunner("",
                "<p>Bacon ipsum dolor amet tri-tip tail pancetta, frankfurter ham hock venison</p>");

        bbTest.textViewTools.navigateToText("hock");
        bbTest.textViewBot.pressShortcut(Keystrokes.F6);
        ViewTestRunner.doPendingSWTWork();

        ViewTestRunner.forceActiveShellHack();
        SWTBotShell sixKeyShell = bbTest.bot.activeShell();
        assertEquals(sixKeyShell.getText(), "Six Key Input");
        SWTBot sixKeyBot = sixKeyShell.bot();
        SWTBotStyledText sixKeyText = sixKeyBot.styledText(0);

        sixKeyText.pressShortcut(KeyStroke.getInstance("F"), KeyStroke.getInstance("J"));
        sixKeyText.pressShortcut(KeyStroke.getInstance("D"), KeyStroke.getInstance("K"));
        ViewTestRunner.doPendingSWTWork();
        sixKeyBot.button("Insert Inline").click();
        ViewTestRunner.doPendingSWTWork();

        MatcherAssert.assertThat(bbTest.textViewWidget.getText(), containsString("c3hock"));
    }
}
