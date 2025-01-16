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

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Builder;

import org.brailleblaster.utd.matchers.XPathMatcher;
import org.testng.annotations.Test;

public class GuideWordsTest {
    private final static String XML_DATA_PATH = "/org/brailleblaster/utd/simpleGlossary.xml";
    Document document;
    UTDTranslationEngine engine = new UTDTranslationEngine();


    public void documentBuilder() {
        StyleMap styleMap = new StyleMap();

        Element root = new Element("root");
        document = new Document(root);

        Element rearmatter = new Element("rearmatter");
        root.appendChild(rearmatter);

        Style style = new Style();
        style.setLinesAfter(5);
        style.setGuideWords(true);
        Element dl = new Element("dl");
        styleMap.put(new XPathMatcher("self::dl"), style);
        Node dt = new Element("dt");
        Node dd = new Element("dd");
        dl.appendChild(dt);
        dl.appendChild(dd);

//		Node newpage2 = new Element("newpage");
//		dl.appendChild(newpage2);
//		dl.appendChild(new Element("test"));

        Node dt2 = new Element("dt");
        Node dd2 = new Element("dd");
        dl.appendChild(dt2);
        dl.appendChild(dd2);

        Node dt3 = new Element("dt");
        Node dd3 = new Element("dd");
        dl.appendChild(dt3);
        dl.appendChild(dd3);

        Node newpage = new Element("newpage");
        dl.appendChild(newpage);

        Element dt4 = new Element("dt");
        dt4.appendChild("DT4");
        Node dd4 = new Element("dd");
        dl.appendChild(dt4);
        dl.appendChild(dd4);

        Element dt5 = new Element("dt");
        Element dd5 = new Element("dd");
        dd5.appendChild(new Element("newpage"));
        dl.appendChild(dt5);
        dl.appendChild(dd5);

        Element dt6 = new Element("dt");
        Node dd6 = new Element("dd");
        dl.appendChild(dt6);
        dl.appendChild(dd6);

        Node inlineNP = new Element("newpage");
//		dl.appendChild(inlineNP);
        Element dt7 = new Element("dt");
        dt7.appendChild(inlineNP);
        Node dd7 = new Element("dd");
        dl.appendChild(dt7);
        dl.appendChild(dd7);

        Element dt8 = new Element("dt");
        dt8.appendChild(new Element("test"));
        Node dd8 = new Element("dd");
        dl.appendChild(dt8);
        dl.appendChild(dd8);

        Element dt9 = new Element("dt");
        Node dd9 = new Element("dd");
        dl.appendChild(dt9);
        dl.appendChild(dd9);


        rearmatter.appendChild(dl);
        engine.setStyleMap(styleMap);
    }

    @Test
    public void testApplyGuide() {
        documentBuilder();
        GuideWords guide = new GuideWords();
        engine = new UTDTranslationEngine();

        Node dl = document.getRootElement().getChild(0).getChild(0);

        guide.findGuideWords(dl, engine);

        //First and last
        Nodes firstLast = dl.query("child::*[name()='dt'][position() = 1 or position() = last()]");
        assertEquals(firstLast.size(), 2);
        for (int i = 0; i < firstLast.size(); i++) {
            assertEquals(firstLast.get(i).toXML(), "<dt />");
            assertTrue(engine.getStyleMap().findValueOrDefault(firstLast.get(i)).isGuideWords());
        }

        //Preceding
        String xpathPre = "child::*[preceding::*[name()='newpage']][name()='dt']"
                + "[preceding-sibling::*[1][name()!='dd']]";
        Nodes preceding = dl.query(xpathPre);
        assertEquals(preceding.size(), 1);
        for (int i = 0; i < preceding.size(); i++) {
            assertEquals(firstLast.get(i).toXML(), "<dt />");
            assertTrue(engine.getStyleMap().findValueOrDefault(preceding.get(i)).isGuideWords());
        }

        //Following
        String xpathFoll = "child::*[name()='dt'][following::*[name()='newpage']]"
                + "[following-sibling::*[2][name()!='dt']]";
        Nodes following = dl.query(xpathFoll);
        assertEquals(following.size(), 1);
        for (int i = 0; i < following.size(); i++) {
            assertEquals(firstLast.get(i).toXML(), "<dt />");
            assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
        }

        //Descendants of <dt>
        String xpathDesc = "child::*[name()='dt'][descendant::*[name()='newpage']]";
        Nodes descendant = dl.query(xpathDesc);
        assertEquals(descendant.size(), 1);
        assertEquals(descendant.get(0).toXML(), "<dt><newpage /></dt>");
        for (int i = 0; i < descendant.size(); i++) {
            assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
            String xpathSib = "following-sibling::*[name()='dt'][1]";
            Nodes sibling = descendant.get(i).query(xpathSib);
            assertEquals(sibling.size(), 1);
            assertEquals(sibling.get(0).toXML(), "<dt><test /></dt>");
            for (int j = 0; j < sibling.size(); j++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(sibling.get(i)).isGuideWords());
            }
        }

        //Descendants of <dd>
        String xpathDesc2 = "child::*[name()='dd'][descendant::*[name()='newpage']]";
        Nodes DDdescendant = dl.query(xpathDesc2);
        assertEquals(DDdescendant.size(), 1);
        assertEquals(DDdescendant.get(0).toXML(), "<dd><newpage /></dd>");
        for (int i = 0; i < DDdescendant.size(); i++) {
            assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
            String xpathSib = "following-sibling::*[name()='dt'][1]";
            Nodes siblingF = DDdescendant.get(i).query(xpathSib);
            assertEquals(siblingF.size(), 1);
            for (int j = 0; j < siblingF.size(); j++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(siblingF.get(i)).isGuideWords());
            }

            String xpathSib2 = "preceding-sibling::*[name()='dt'][1]";
            Nodes siblingD = DDdescendant.get(i).query(xpathSib2);
            assertEquals(siblingD.size(), 1);
            for (int j = 0; j < siblingD.size(); j++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(siblingD.get(i)).isGuideWords());
            }
        }


    }

    @Test
    public void testFindGuideWords() throws Exception {
        documentBuilder();
        GuideWords guide = new GuideWords();
        Builder builder = new Builder();

        try (InputStream in = getClass().getResourceAsStream(XML_DATA_PATH)) {
            Document document = builder.build(in);
            Node dl = document.query("descendant::dl").get(0);

            guide.findGuideWords(dl, engine);

            //First and last
            Nodes firstLast = dl.query("child::*[name()='dt'][position() = 1 or position() = last()]");
            assertEquals(firstLast.size(), 2);
            for (int i = 0; i < firstLast.size(); i++) {
                if (i == 0) {
                    assertEquals(firstLast.get(i).toXML(), "<dt>One</dt>");
                } else {
                    assertEquals(firstLast.get(i).toXML(), "<dt>Eight</dt>");
                }

                assertTrue(engine.getStyleMap().findValueOrDefault(firstLast.get(i)).isGuideWords());
            }

            //Preceding
            String xpathPre = "child::*[name()='dt'][preceding::*[name()='newpage']]"
                    + "[preceding-sibling::*[1][name()!='dd']]";
            Nodes preceding = dl.query(xpathPre);
            assertEquals(preceding.size(), 1);
            assertEquals(preceding.get(0).toXML(), "<dt>Three</dt>");
            for (int i = 0; i < preceding.size(); i++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(preceding.get(i)).isGuideWords());
            }

            //Following
            String xpathFoll = "child::*[name()='dt'][following::*[name()='newpage']]"
                    + "[following-sibling::*[2][name()!='dt']]";
            Nodes following = dl.query(xpathFoll);
            assertEquals(following.size(), 1);
            assertEquals(following.get(0).toXML(), "<dt>Two</dt>");
            for (int i = 0; i < following.size(); i++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
            }

            //Descendants of <dt>
            String xpathDesc = "child::*[name()='dt'][descendant::*[name()='newpage']]";
            Nodes descendant = dl.query(xpathDesc);
            assertEquals(descendant.size(), 1);
            assertEquals(descendant.get(0).toXML(), "<dt>Five<newpage /></dt>");
            for (int i = 0; i < descendant.size(); i++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
                String xpathSib = "following-sibling::*[name()='dt'][1]";
                Nodes sibling = descendant.get(0).query(xpathSib);
                assertEquals(sibling.size(), 1);
                assertEquals(sibling.get(i).toXML(), "<dt>Six</dt>");
                for (int j = 0; j < sibling.size(); j++) {
                    assertTrue(engine.getStyleMap().findValueOrDefault(sibling.get(i)).isGuideWords());
                }
            }

            //Descendants of <dd>
            String xpathDesc2 = "child::*[name()='dd'][descendant::*[name()='newpage']]";
            Nodes DDdescendant = dl.query(xpathDesc2);
            assertEquals(DDdescendant.size(), 1);
            assertEquals(DDdescendant.get(0).toXML(), "<dd>The number 7.<newpage /></dd>");
            for (int i = 0; i < DDdescendant.size(); i++) {
                assertTrue(engine.getStyleMap().findValueOrDefault(following.get(i)).isGuideWords());
                String xpathSib = "following-sibling::*[name()='dt'][1]";
                Nodes siblingF = DDdescendant.get(i).query(xpathSib);
                assertEquals(siblingF.size(), 1);
                assertEquals(siblingF.get(i).toXML(), "<dt>Eight</dt>");
                for (int j = 0; j < siblingF.size(); j++) {
                    assertTrue(engine.getStyleMap().findValueOrDefault(siblingF.get(i)).isGuideWords());
                }

                String xpathSib2 = "preceding-sibling::*[name()='dt'][1]";
                Nodes siblingD = DDdescendant.get(i).query(xpathSib2);
                assertEquals(siblingD.size(), 1);
                assertEquals(siblingD.get(i).toXML(), "<dt>Seven</dt>");
                for (int j = 0; j < siblingD.size(); j++) {
                    assertTrue(engine.getStyleMap().findValueOrDefault(siblingD.get(i)).isGuideWords());
                }
            }

            /*
             *  Find total number of guide words.
             *  This should be 7 based on the simpleGlossary.xml
             */
//			assertEquals(engine.getStyleMap().findValue(allDT.get(3)).isGuideWords(), null);

            //Important
//			assertEquals(count, 7);


        }
    }

}
