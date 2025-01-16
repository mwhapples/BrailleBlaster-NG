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

import java.io.StringReader;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import org.brailleblaster.utd.internal.xml.XMLHandler;
import org.brailleblaster.utd.utils.UTDHelper;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class NormaliserTest {
    @DataProvider
    private Object[][] spaceDataProvider() {
        char BRLSPACE = UTDHelper.BRAILLE_SPACE;
        return new Object[][]{
                //Just the text
                new Object[]{"<p>This is some text that has spaces</p>"},
                //Spaces around elements
                new Object[]{"<p><em>This</em> is some text that has spaces</p>"},
                new Object[]{"<p><em>This </em>is some text that has spaces</p>"},
                new Object[]{"<p><em>This is</em> some text that has spaces</p>"},
                new Object[]{"<p><em>This is </em>some text that has spaces</p>"},
                new Object[]{"<p>This is some <em>text</em> that has spaces</p>"},
                new Object[]{"<p>This is some<em> text</em> that has spaces</p>"},
                new Object[]{"<p>This is some <em>text </em>that has spaces</p>"},
                new Object[]{"<p>This is some text<em> that </em>has spaces</p>"},
                //Spaces in between elements
                new Object[]{"<p>This is some text<em> that </em><strong>has </strong>spaces</p>"},
                new Object[]{"<p>This is some text<em> that</em> <strong>has </strong>spaces</p>"},
                new Object[]{"<p>This is <em>some</em> <strong>text</strong> <em>that</em> has spaces</p>"},
                //Blank elements
                new Object[]{"<p>This is <br/>some text that has spaces</p>"},
                new Object[]{"<p>This is<br/> some text that has spaces</p>"},
                new Object[]{"<p>This is some<br/><br/><br/><br/> text that has spaces</p>"},
                new Object[]{"<p><br/>This is some text that has spaces</p>"},
                new Object[]{"<p>This is some text that has spaces<br/></p>"},
                //Inline
                new Object[]{"<p>This is <em>s</em>ome text that has spaces</p>"},
                new Object[]{"<p>This is s<em>o</em>me text that has spaces</p>"},
                new Object[]{"<p>This is som<em>e</em> text that has spaces</p>"},
                //Wrapped in element with spaces
                new Object[]{"<p>\n    <em>This is some text that has spaces</em></p>"},
                new Object[]{"<p><em>This is some text that has spaces    </em>\n    </p>"},
                new Object[]{"<p><em>This is some text that has spaces&#x2003;&#x2003;&#x2003;&#x2003;</em>\n    </p>"},
                new Object[]{"<p><em>This is some text that has spaces" + BRLSPACE + BRLSPACE + BRLSPACE + "</em>\n    </p>"},
                //Spaces between paragraphs (split word will get re-merged)
                new Object[]{"<p>This is some te</p>\n    <p>xt that has spaces</p>"},
                new Object[]{"<p>This is some text \n     <b>that has spaces</b></p>"},
                new Object[]{"<p>This is some text \n<b>that has spaces</b></p>"},
                //Spaces and the beggining and ending of real text
                //DISABLED: There are many cases where the spaces might be relevant, and real books shouldn't contain this
                //Oct 12, 2015: Renabled, document exact edge cases here if disabling again
                new Object[]{"<p><em>This is some text that has spaces    </em></p>"},
                new Object[]{"<p><em>    This is some text that has spaces</em></p>"},
                //Surrounded by indentation
                //DISABLED: Those might be relevant, no real book should have this
                //Oct 11, 2015: Renabled
                new Object[]{"<p>    This is some text that has spaces    </p>"},
                new Object[]{"<p>\t\t\tThis is some text that has spaces\t\t\t</p>"},
                new Object[]{"<p>\t\n\t\r\tThis is some text that has spaces\t\r\t\n\t</p>"}, //Spaces in the middle
                //
                //DISABLED Oct 12, 2015: Spaces between elements are tricky
                //eg <p><strong>bold</strong> <strong>text</strong></p>
//			new Object[]{"<p><em>This is some text that has spaces</em>    </p>"}, 
                //
                //DISABLED: Those might be relevant, no real book should have this
                //Oct 13, 2015: Renabled until actual documented issues appear
                new Object[]{"<p>This   is some   text that   has spaces</p>"},
                new Object[]{"<p>\tThis  \t \t  is \n\r some \r  text \n that  \r has spaces</p>"},
                //Spaces around image tags are almost always extraneous
                new Object[]{"<p>This is some text that has spaces <img src='test'/></p>"},
                new Object[]{"<p><img src='test'/> This is some text that has spaces</p>"},
        };
    }

    @Test(dataProvider = "spaceDataProvider")
    public void spaceTest(String xml) {
//		xml = "<?xml version='1.0' encoding='UTF-8'?><?xml-stylesheet href=\"dtbookbasic.css\" type=\"text/css\"?>\n"
//				+ "<!DOCTYPE dtbook PUBLIC '-//NISO//DTD dtbook 2005-3//EN' 'http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd' >"
//				+ "<dtbook xmlns=\"http://www.daisy.org/z3986/2005/dtbook/\" version=\"2005-3\" xml:lang=\"En-US\">\n"
//				+ "<head>\n"
//				+ "</head>"
//				+ "<book>"
//				+ "<frontmatter>"
//				+ "<doctitle></doctitle>\n"
//				+ "<docauthor></docauthor>"
//				+ "<level1>"
//				+ xml
//				+ "</level1></frontmatter></book></dtbook>";
        xml = "<book><level1>" + xml + "</level1></book>";

        Document doc = new XMLHandler().load(new StringReader(xml));

        //Assemble all the text nodes
        StringBuilder text = new StringBuilder();
        Element root = doc.getRootElement().getFirstChildElement("level1");
        for (Node curText : root.query("descendant::text()")) {
            text.append(curText.getValue());
        }

        assertEquals(text.toString(), "This is some text that has spaces", "Failed on " + xml);
    }
}
