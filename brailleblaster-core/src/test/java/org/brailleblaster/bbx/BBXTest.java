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
package org.brailleblaster.bbx;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BBXTest {
	private static final Logger log = LoggerFactory.getLogger(BBXTest.class);	
	
	@DataProvider
	public Object[][] coreTypeDataProvider() {
		return new Object[][]{
			new Object[]{BBX.SECTION},
			new Object[]{BBX.CONTAINER},
			new Object[]{BBX.BLOCK},
			new Object[]{BBX.INLINE},
			new Object[]{BBX.SPAN},};
	}

	@Test(dataProvider = "coreTypeDataProvider")
	public void subTypeNamesMatchFieldNames(BBX.CoreType type) throws Exception {
		for (Field curField : type.getClass().getDeclaredFields()) {
			log.debug("Field {}", curField);
			if (!BBX.SubType.class.isAssignableFrom(curField.getType())) {
				continue;
			}
			Assert.assertTrue(Modifier.isPublic(curField.getModifiers()), "subtype " + curField + " not accessible");
			
			Assert.assertEquals(((BBX.SubType)curField.get(type)).name, curField.getName(), "Failed on " + curField);
		}
	}

	@Test
	public void coreTypeNamesMatchFieldNames() throws Exception {
		for (Field curField : BBX.class.getDeclaredFields()) {
			log.debug("Field {}", curField);
			if (!BBX.CoreType.class.isAssignableFrom(curField.getType())) {
				continue;
			}
			Assert.assertTrue(Modifier.isPublic(curField.getModifiers()), "subtype " + curField + " not accessible");
			
			Assert.assertEquals(((BBX.CoreType)curField.get(null)).name, curField.getName(), "Failed on " + curField);
		}
	}
	
	@Test
	public void constructorsHiddenMain() {
		for (Constructor<?> curConstructor : BBX.class.getDeclaredConstructors()) {
			Assert.assertFalse(Modifier.isPublic(curConstructor.getModifiers()));
		}
	}
	
	@Test(dataProvider = "coreTypeDataProvider")
	public void constructorsHiddenCoreType(BBX.CoreType type) {
		for (Constructor<?> curConstructor : type.getClass().getDeclaredConstructors()) {
			Assert.assertFalse(Modifier.isPublic(curConstructor.getModifiers()));
		}
	}
	
	@Test(dataProvider = "coreTypeDataProvider")
	public void constructorsHiddenSubType(BBX.CoreType type) throws Exception {
		for (Field curField : type.getClass().getFields()) {
			Object value = curField.get(type);
			if (!BBX.SubType.class.isAssignableFrom(value.getClass())) {
				continue;
			}
			
			for (Constructor<?> curSubTypeConstructor : value.getClass().getDeclaredConstructors()) {
				log.debug("Field: " + curSubTypeConstructor);
				Assert.assertFalse(Modifier.isPublic(curSubTypeConstructor.getModifiers()), curSubTypeConstructor.toString());
			}
		}
	}
	
	@Test(dataProvider = "coreTypeDataProvider")
	public void subtypesListMatchesField(BBX.CoreType type) throws Exception {
		int counter = 0;
		for (Field curField : type.getClass().getFields()) {
			if (!BBX.SubType.class.isAssignableFrom(curField.getType())) {
				continue;
			}
			
			Assert.assertEquals(curField.get(type), type.subTypes.get(counter++), "subTypes list doesn't match fields");
		}
	}
}
