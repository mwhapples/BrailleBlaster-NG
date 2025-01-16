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

import com.google.common.collect.Lists;
import org.brailleblaster.utd.config.UTDConfig;
import org.brailleblaster.utd.testutils.UTDConfigUtils;
import org.brailleblaster.libembosser.spi.BrlCell;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class BrailleSettingsTest {
    private BrailleSettings braille;

    @BeforeMethod
    public void setBrailleSettings() {
        braille = new BrailleSettings();
    }

    @Test
    public void defaultConstructor() {
        //Disabled to stop this test failing when the tables are changed (eg us to UEB)
//		assertEquals(braille.getMainTranslationTable(), "en-us-g2.ctb");
        assertFalse(braille.isUseAsciiBraille());
        assertEquals(braille.getCellType(), BrlCell.NLS);
        assertTrue(braille.getMathLineWrapping().isEmpty());
    }

    @Test
    public void testSetters() {
        boolean setUseAsciiBraille = true;
        braille.setUseAsciiBraille(setUseAsciiBraille);
        boolean getUseAsciiBraille = braille.isUseAsciiBraille();
        assertEquals(getUseAsciiBraille, setUseAsciiBraille);

        String setMainTranslationTable = "nemeth.ctb";
        braille.setMainTranslationTable(setMainTranslationTable);
        String getMainTranslationTable = braille.getMainTranslationTable();
        assertEquals(getMainTranslationTable, setMainTranslationTable);

        BrlCell setCellType = BrlCell.SMALL_ENGLISH;
        braille.setCellType(setCellType);
        BrlCell getCellType = braille.getCellType();
        assertEquals(getCellType, setCellType);

        List<InsertionPatternEntry> setMathLineWrapping = Lists.newArrayList(new InsertionPatternEntry("ab", "\""), new InsertionPatternEntry("bc", "\""));
        braille.setMathLineWrapping(setMathLineWrapping);
        List<InsertionPatternEntry> getMathLineWrapping = Lists.newArrayList(new InsertionPatternEntry("ab", "\""), new InsertionPatternEntry("bc", "\""));
        assertEquals(braille.getMathLineWrapping(), getMathLineWrapping);

    }

    /**
     * Disabled to stop this test failing when the tables are changed (eg us to UEB)
     *
     * @throws IOException
     */
    @Test(enabled = false)
    public void saveEngine() throws IOException {
        BrailleSettings brailleSettings = new BrailleSettings();
        assertNotSame(brailleSettings.getCellType(), BrlCell.SMALL_ENGLISH, "Defaults changed, update test");
        brailleSettings.setCellType(BrlCell.SMALL_ENGLISH);
        brailleSettings.setMathLineWrapping(Lists.newArrayList(new InsertionPatternEntry(" (\\.k )", "\""), new InsertionPatternEntry("-", "\"")));

        File tempOutput = File.createTempFile("utdEngine", "test");
        UTDConfig.saveBrailleSettings(tempOutput, brailleSettings);
        UTDConfigUtils.compareOutputToSaved(tempOutput, UTDConfigUtils.TEST_BRAILLE_SETTINGS_FILE);
    }

    @Test
    public void loadEngine() {
        BrailleSettings brailleSettings = UTDConfig.loadBrailleSettings(UTDConfigUtils.TEST_BRAILLE_SETTINGS_FILE);
        assertEquals(brailleSettings.getCellType(), BrlCell.SMALL_ENGLISH);
        assertEquals(brailleSettings.getMathLineWrapping(), Lists.newArrayList(new InsertionPatternEntry(" (\\.k )", "\""), new InsertionPatternEntry("-", "\"")));
    }
}
