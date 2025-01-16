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
package org.brailleblaster.utd.testutils.asserts;

import nu.xom.Node;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Fail;
import org.assertj.core.util.Objects;
import org.brailleblaster.utd.TextSpan;

public class TextSpanAssert extends AbstractAssert<TextSpanAssert, TextSpan> {
    protected TextSpanAssert(TextSpan actual) {
        super(actual, TextSpanAssert.class);
    }

    public static TextSpanAssert assertThat(TextSpan actual) {
        return new TextSpanAssert(actual);
    }

    public TextSpanAssert isTranslated() {
        isNotNull();
        if (!actual.isTranslated()) {
            Fail.fail("Expected span to be translated");
        }
        return this;
    }

    public TextSpanAssert isNotTranslated() {
        isNotNull();
        if (actual.isTranslated()) {
            Fail.fail("Expected span not to be translated but it is translated");
        }
        return this;
    }

    public TextSpanAssert hasText(String text) {
        isNotNull();
        if (!StringUtils.equals(actual.getText(), text)) {
            failWithMessage("Expected text to be <%s> but it was <%s>", text, actual.getText());
        }
        return this;
    }

    public TextSpanAssert hasNode(Node node) {
        isNotNull();
        if (!(Objects.areEqual(actual.getNode(), node))) {
            failWithMessage("Expected node to be <[%s] but was [%s]", node.toXML(), actual.getNode().toXML());
        }
        return this;
    }

    public TextSpanAssert hasBrlElementXML(String expectedBrlXML) {
        isNotNull();
        if (actual.getBrlElement() != null) {
            String actualBrlXML = actual.getBrlElement().toXML();
            if (!actualBrlXML.equals(expectedBrlXML)) {
                failWithMessage("Expected brl element XML to be [%s] but it was [%s]", expectedBrlXML, actualBrlXML);
            }
        } else if (expectedBrlXML != null) {
            failWithMessage("Expected brl element XML was [%s] but it was null", expectedBrlXML);
        }
        return this;
    }
}
