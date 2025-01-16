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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.brailleblaster.utd.internal.NormaliserFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

public class FormatSelectorTest {
    @Test
    public void defaultConstructor() {
        FormatSelector formatter = new FormatSelector(mock(ITranslationEngine.class));
        assertNotNull(formatter.styleStack);
        assertNotNull(formatter.styleMap);
    }

    @Test
    public void constructWithValues() {
        IStyleMap styleMap = new StyleMap();
        StyleStack styleStack = new StyleStack();
        FormatSelector formatter = new FormatSelector(styleMap, styleStack, mock(ITranslationEngine.class));
        assertSame(formatter.styleMap, styleMap);
        assertSame(formatter.styleStack, styleStack);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void constructWithNullStyleMap() {
        new FormatSelector(null, new StyleStack(), mock(ITranslationEngine.class));
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void constructWithNullStyleStack() {
        new FormatSelector(new StyleMap(), null, mock(ITranslationEngine.class));
    }

    @Test
    public void getStyleStackAlwaysSameObject() {
        FormatSelector formatter = new FormatSelector(mock(ITranslationEngine.class));
        IStyle result1 = formatter.styleStack;
        IStyle result2 = formatter.styleStack;
        assertSame(result1, result2);
    }

    @DataProvider(name = "formatDocumentProvider")
    public Iterator<Object[]> formatDocumentProvider() throws ParsingException, IOException {
        List<Object[]> dataList = new ArrayList<>();
        Builder builder = new Builder(new NormaliserFactory());
        try (InputStream inStream = getClass().getResourceAsStream("/org/brailleblaster/utd/formatDocument.xml")) {
            Document doc = builder.build(inStream);
            dataList.add(new Object[]{doc.getRootElement()});
        }
        return dataList.iterator();
    }

    @Test(dataProvider = "formatDocumentProvider")
    public void formatDocumentCallStyleActions(Element e) {
        Document doc = e.getDocument();
        if (doc == null) {
            doc = new Document(e);
        }
        UTDTranslationEngine engine = new UTDTranslationEngine();
        FormatSelector formatter = new FormatSelector(engine);
        PageBuilder pageBuilder = new PageBuilder(engine, new Cursor());
        Set<PageBuilder> pbs = new LinkedHashSet<>();
        pbs.add(pageBuilder);
        PageBuilder secondPageBuilder = formatter.formatNode(doc, pbs);
        assertSame(pageBuilder, secondPageBuilder);
        // TODO: Complete test
    }
}
