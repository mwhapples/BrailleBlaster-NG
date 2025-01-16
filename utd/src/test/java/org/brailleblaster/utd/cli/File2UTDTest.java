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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.brailleblaster.utd.testutils.UTDConfigUtils;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

public class File2UTDTest {
	
	@Test
	public void parseBasicDTBook() throws Exception {
		LoggerFactory.getLogger(getClass()).debug("Path: " + System.getProperty("jlouis.data.path"));
		// Copy the content of the input to a temp file as potentially could be in a jar.
		// Also fixes the issue of building from a path containing a space.
		// (See: RT4407)
		File tempIn = File.createTempFile("File2UTD", "basicDTBook.xml");
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(tempIn);
			Resources.copy(getClass().getResource("basicDTBook.xml"), outStream);
		} finally {
			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
		}
		String input = tempIn.getAbsolutePath();
		String output = File.createTempFile("file2utd", "basicDTBook").toString();
		
		File2UTD app = new File2UTD(input, output, null, 
				UTDConfigUtils.TEST_ACTION_FILE.getAbsolutePath(),
				UTDConfigUtils.TEST_STYLE_FILE.getAbsolutePath(),
				UTDConfigUtils.TEST_STYLEDEFS_FILE.getAbsolutePath(), false);
		app.run();
	}
}
