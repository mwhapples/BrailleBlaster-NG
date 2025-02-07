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
package org.brailleblaster.perspectives.braille.document;

import org.brailleblaster.TestUtils;
import org.brailleblaster.search.SearchConstants;
import org.brailleblaster.testrunners.BBTestRunner;
import org.eclipse.swt.SWT;

public class FindReplaceBotHelper {
    public static void modifyReplace(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).buttonInGroup(SearchConstants.MODIFY, SearchConstants.REPLACE).click();
    }

    public static void modifyFind(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).buttonInGroup(SearchConstants.MODIFY, SearchConstants.FIND).click();
    }

    public static void resetReplace(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).buttonInGroup(SearchConstants.RESET, SearchConstants.REPLACE).click();
    }

    public static void done(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.DONE).click();
    }

    public static void clickFormatting(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.FORMATTING).click();
    }

    public static void openDialog(BBTestRunner bb) {
        bb.textViewTools.pressShortcut(SWT.CTRL, 'f');
    }

    public static void clickFind(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.FIND).click();
    }

    public static void clickReplaceAll(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.REPLACE_ALL).click();
    }

    public static void clickReplace(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.REPLACE_FIND).click();
    }

    public static void addTextAttributes(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).radioInGroup(SearchConstants.ADD, SearchConstants.TEXT_ATTRIBUTES).click();
    }

    public static void removeTextAttributes(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).radioInGroup(SearchConstants.REMOVE, SearchConstants.TEXT_ATTRIBUTES).click();
    }

    public static void addStylesAndContainers(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).radioInGroup(SearchConstants.ADD, SearchConstants.STYLES_AND_CONTAINERS).click();
    }

    public static void checkMatchCase(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).checkBox(SearchConstants.MATCH_CASE).click();
    }

    public static void clickOk(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.OK).click();
    }

    public static void clickReset(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.RESET).click();
    }

    public static void clickClose(BBTestRunner bb) {
        TestUtils.refreshReturnActiveBot(bb).button(SearchConstants.CLOSE).click();
    }
}
