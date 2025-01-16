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
package org.brailleblaster.testrunners;

import java.util.function.Consumer;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;
import org.brailleblaster.bbx.BBX;

public class BBXDocFactory {
	public final Element root;

	public BBXDocFactory() {
		this(BBX.FORMAT_VERSION);
	}

	public BBXDocFactory(int version) {
		Document newDocument = BBX.newDocument();
		if (BBX.FORMAT_VERSION != version) {
			BBX.setFormatVersion(newDocument, version);
		}
		newDocument.getRootElement().appendChild(BBX.SECTION.ROOT.create());
		root = BBX.getRoot(newDocument);
	}

	public BBXDocFactory(Element root) {
		this.root = root;
	}

	public BBXDocFactory runOnThis(Consumer<BBXDocFactory> thisFactory) {
		thisFactory.accept(this);
		return this;
	}
	
	public BBXDocFactory append(Element elem) {
		root.appendChild(elem);
		return this;
	}

	public BBXDocFactory append(String text) {
		root.appendChild(new Text(text));
		return this;
	}

	public BBXDocFactory append(Element elem, Consumer<BBXDocFactory> child) {
		append(elem);
		child.accept(new BBXDocFactory(elem));
		return this;
	}

	public BBXDocFactory append(Element elem, String childText) {
		elem.appendChild(childText);
		return append(elem);
	}

	//.....elements......
	public <T> BBXDocFactory addAttribute(BBX.BaseAttribute<T> attrib, T value) {
		attrib.set(root, value);
		return this;
	}
}
