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
package org.brailleblaster.frontmatter;

import com.google.common.collect.Iterables;
import nu.xom.Document;
import nu.xom.Element;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.brailleblaster.BBIni;
import org.brailleblaster.archiver2.Archiver2;
import org.brailleblaster.bbx.BBX;
import org.brailleblaster.utd.utils.BBX2PEFConverterKt;
import org.brailleblaster.utils.localization.LocaleHandler;
import org.brailleblaster.perspectives.braille.Manager;
import org.brailleblaster.settings.UTDManager;
import org.brailleblaster.utd.BRFWriter;
import org.brailleblaster.utd.UTDTranslationEngine;
import org.brailleblaster.utd.internal.xml.FastXPath;
import org.brailleblaster.utd.properties.UTDElements;
import org.brailleblaster.utd.utils.BBX2PEFConverter;
import org.brailleblaster.utils.swt.EasySWT;
import org.brailleblaster.util.FormUIUtils;
import org.brailleblaster.util.Notify;
import org.brailleblaster.wordprocessor.BBFileDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Save volume to brf dialog
 */
public class VolumeSaveDialog {
    private static final LocaleHandler localeHandler = LocaleHandler.getDefault();

    private static final Logger log = LoggerFactory.getLogger(VolumeSaveDialog.class);
    public static final String SETTINGS_FORMAT = "volumeSaveDialog.format";
    public static final String SWTBOT_SAVE_SINGLE = "volumeSaveDialog.saveAll";
    public static final String SWTBOT_SAVE_FOLDER = "volumeSaveDialog.saveFolder";
    public static final String SWTBOT_SAVE_FOLDER_ALL = "volumeSaveDialog.saveFolderAll";
    private static final String KEY_VOLUME_DATA = "volumeSaveDialog.volumeData";
    private final Archiver2 arch;
    private final UTDManager utdManager;
    private final Document doc;
    private final Manager m;
    private boolean saveExec = false; // To know if the user try to complete the save process
    private final Shell shell;
    private final Table volumesTable;
    private Format selectedFormat = Format.valueOf(
            BBIni.getPropertyFileManager().getProperty(SETTINGS_FORMAT, Format.BRF.toString())
    );

    public VolumeSaveDialog(@NotNull Shell parent, Archiver2 arch, UTDManager utdManager, Document doc, Manager m) {
        this.arch = arch;
        this.utdManager = utdManager;
        this.doc = doc;
        this.m = m;
        List<Element> volumes = VolumeUtils.getVolumeElements(doc);

        shell = FormUIUtils.makeDialog(parent);
        shell.setText(localeHandler.get("&SaveVolumeBRFPEF"));
        shell.setLayout(new GridLayout(2, false));

        if (volumes.isEmpty()) {
            clickSaveSingle();

            volumesTable = null;
            return;
        }

        //Table must be wrapped in a composite for some reason
        Composite tableWrapper = EasySWT.makeComposite(shell, 1);
        EasySWT.buildGridData()
                .setGrabSpace(true, true)
                .setAlign(GridData.FILL, GridData.FILL)
                .verticalSpan(3)
                .applyTo(tableWrapper);

        volumesTable = new Table(tableWrapper, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        EasySWT.buildGridData()
                .setGrabSpace(true, true)
                .setAlign(GridData.FILL, GridData.FILL)
                .applyTo(volumesTable);

        TableColumn name = new TableColumn(volumesTable, SWT.NONE);
        name.setText("Volume");
        name.setWidth(100);

        Button saveSingle = new Button(shell, SWT.NONE);
        EasySWT.INSTANCE.addSwtBotKey(saveSingle, SWTBOT_SAVE_SINGLE);
        saveSingle.setText("Save All to Single File");
        FormUIUtils.setGridData(saveSingle);

        Button saveFolder = new Button(shell, SWT.NONE);
        EasySWT.INSTANCE.addSwtBotKey(saveFolder, SWTBOT_SAVE_FOLDER);
        saveFolder.setText("Save Selected to Folder");
        FormUIUtils.setGridData(saveFolder);

        Button saveFolderAll = new Button(shell, SWT.NONE);
        EasySWT.INSTANCE.addSwtBotKey(saveFolderAll, SWTBOT_SAVE_FOLDER_ALL);
        saveFolderAll.setText("Save All to Folder");
        FormUIUtils.setGridData(saveFolderAll);

        // ----------------- Listeners --------------------
        FormUIUtils.addSelectionListener(saveSingle, e -> clickSaveSingle());
        FormUIUtils.addSelectionListener(saveFolder, e -> clickSaveFolder(false));
        FormUIUtils.addSelectionListener(saveFolderAll, e -> clickSaveFolder(true));

        // -------------------- Data ---------------------
        for (VolumeUtils.VolumeData curVolume : VolumeUtils.INSTANCE.getVolumeNames(volumes)) {
            TableItem entry = new TableItem(volumesTable, SWT.NONE);
            entry.setText(new String[]{curVolume.nameLong});
            entry.setData(KEY_VOLUME_DATA, curVolume);
        }

        FormUIUtils.setLargeDialogSize(shell);
        shell.open();
    }

    private void clickSaveSingle() {
        log.trace("saving volumneless brf");

        Pair<Format, String> save = generateSaveDialog();
        if (save == null) {
            log.debug("canceled save");
            return;
        }
        Format format = save.getKey();
        String path = save.getValue();
        try {
            if (format == Format.BRF) {
                utdManager.getEngine().toBRF(doc, new File(path));
            } else if (format == Format.PEF) {
                OutputStream os = new FileOutputStream(path);
                String defaultIdentifier = identifierFromFileName(new File(path));
                BBX2PEFConverterKt.convertBBX2PEF(doc, defaultIdentifier, utdManager.getEngine(), BBX2PEFConverterKt.ALL_VOLUMES, os);
            } else {
                throw new UnsupportedOperationException("unknown format " + format);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to write BRF to " + path, e);
        }
    }

    private void clickSaveFolder(boolean all) {
        log.trace("saving BRF to folder");
        TableItem[] selectedItems = all
                ? volumesTable.getItems()
                : volumesTable.getSelection();
        if (selectedItems.length == 0) {
            Notify.showMessage("Must select volume");
            return;
        }

        String path;
        if (selectedItems.length == 1) {
            Pair<Format, String> save = generateSaveDialog();
            if (save == null) {
                log.debug("canceled save");
                return;
            }
            Format format = save.getKey();
            path = save.getValue();

            int volumeIndex = ArrayUtils.indexOf(volumesTable.getItems(), selectedItems[0]);
            try {
                if (format == Format.BRF) {
                    String volumeBRF = volumeToBRF(utdManager.getEngine(), doc, volumeIndex, true);
                    FileUtils.write(new File(path), volumeBRF, StandardCharsets.UTF_8);
                    log.info("Wrote {} characters to {}", volumeBRF.length(), path);
                } else if (format == Format.PEF) {
                    String defaultIdentifier = identifierFromFileName(new File(path));
                    BBX2PEFConverterKt.convertBBX2PEF(doc, defaultIdentifier, utdManager.getEngine(), x -> x == volumeIndex, new FileOutputStream(path));
                } else {
                    throw new UnsupportedOperationException("unknown format " + format);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to save ordinal volume " + volumeIndex, e);
            }
            shell.close();
        } else {
            final Path debugSavePath = BBIni.getDebugSavePath();
            if (BBIni.getDebugging() && debugSavePath != null) {
                path = debugSavePath.getParent().toAbsolutePath().toString();
            } else {
                DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE);
                path = dialog.open();
            }

            if (path == null) {
                log.debug("canceled save");
                return;
            }

            //Always prompt for file type to save
            Shell formatSelector = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
            formatSelector.setLayout(new GridLayout(1, false));
            formatSelector.setText("Format selector");

            Button formatBrf = new Button(formatSelector, SWT.RADIO);
            formatBrf.setText("Braille Ready File (BRF)");

            Button formatPef = new Button(formatSelector, SWT.RADIO);
            formatPef.setText("Portable Embosser Format (PEF)");

            Button submit = new Button(formatSelector, SWT.NONE);
            submit.setText("Submit");

            formatSelector.open();
            formatSelector.pack();

            // listeners
            submit.addListener(SWT.Selection, e -> {
                if (formatBrf.getSelection()) {
                    selectedFormat = Format.BRF;
                } else if (formatPef.getSelection()) {
                    selectedFormat = Format.PEF;
                } else {
                    throw new UnsupportedOperationException("missing selection");
                }

                doSaveFolder(selectedItems, path);
                formatSelector.close();
                if (saveExec)    // if the user press No, shell will remain open
                    shell.close();
            });

            // data
            if (selectedFormat == Format.BRF) {
                formatBrf.setSelection(true);
            } else if (selectedFormat == Format.PEF) {
                formatPef.setSelection(true);
            } else {
                throw new UnsupportedOperationException("unknown format");
            }
        }
    }

    private void doSaveFolder(TableItem[] selectedItems, String path) {
        int checkvalue = checkDoSaveFolder(selectedItems, path);
        if (checkvalue == 0) {  //the user press Yes
            saveExec = true;
            for (TableItem selectedItem : selectedItems) {
                VolumeUtils.VolumeData volumeData = (VolumeUtils.VolumeData) selectedItem.getData(KEY_VOLUME_DATA);
                int volumeIndex = ArrayUtils.indexOf(volumesTable.getItems(), selectedItem);
                try {
                    File outputPath = getBRFPath(
                            Paths.get(path),
                            arch.getPath(),
                            volumeData.type,
                            volumeData.volumeTypeIndex,
                            selectedFormat
                    );

                    if (selectedFormat == Format.BRF) {
                        String volumeBRF = volumeToBRF(utdManager.getEngine(), doc, volumeIndex, true);
                        FileUtils.write(
                                outputPath,
                                volumeBRF,
                                StandardCharsets.UTF_8
                        );
                        log.info("Wrote {} characters to {}", volumeBRF.length(), outputPath);
                    } else if (selectedFormat == Format.PEF) {
                        String defaultIdentifier = identifierFromFileName(outputPath);
                        BBX2PEFConverterKt.convertBBX2PEF(doc, defaultIdentifier, utdManager.getEngine(), x -> x == volumeIndex, new FileOutputStream(outputPath));
                        log.info("Wrote volume {} to PEF", volumeIndex);
                    } else {
                        throw new UnsupportedOperationException("unknown format");
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Unable to save ordinal volume " + volumeIndex, e);
                }
            }
        }

    }

    private String identifierFromFileName(File path) {
        String identifier = path.getName();
        if (identifier.contains(".")) {
            identifier = identifier.substring(0, identifier.lastIndexOf("."));
        }
        return identifier;
    }

    private int checkDoSaveFolder(TableItem[] selectedItems, String path) {
        boolean existsFile = false;
        for (TableItem selectedItem : selectedItems) {
            VolumeUtils.VolumeData volumeData = (VolumeUtils.VolumeData) selectedItem.getData(KEY_VOLUME_DATA);

            File outputPath = getBRFPath(
                    Paths.get(path),
                    arch.getPath(),
                    volumeData.type,
                    volumeData.volumeTypeIndex,
                    selectedFormat
            );
            if (Files.exists(outputPath.toPath())) {
                existsFile = true;
                break;
            }

        }
        if (existsFile) {
            MessageDialog dialog = new MessageDialog(shell, "Confirm Save", null,
                    "This operation will overwrite one or more files in this directory. Are you sure you want to continue?", MessageDialog.WARNING, new String[]{"Yes",
                    "No"}, 0);
            return dialog.open(); //return 0 Yes, 1 No, -1 close dialog
        } else {
            return 0;
        }
    }

    public static File getBRFPath(Path documentPath, BBX.VolumeType volumeType, int volumeTypeIndex, Format selectedFormat) {
        return getBRFPath(documentPath.getParent(), documentPath, volumeType, volumeTypeIndex, selectedFormat);
    }

    public static File getBRFPath(Path parentFile, Path documentPath, BBX.VolumeType volumeType, int volumeTypeIndex, Format selectedFormat) {
        //Strip off extension
        String name = documentPath.getFileName().toString();
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        String format = ".brf";
        if (selectedFormat == Format.PEF) {
            format = ".pef";
        }

        name = name
                + "_"
                + volumeType.volumeNameShort.toLowerCase()
                + volumeTypeIndex
                + format;
        return parentFile.resolve(name).toFile();
    }

    @Nullable
    private Pair<Format, String> generateSaveDialog() {
        final Path debugSavePath = BBIni.getDebugSavePath();
        if (BBIni.getDebugging() && debugSavePath != null) {
            // TODO: PEF testing
            return Pair.of(Format.BRF, debugSavePath.toString());
        } else {
            String fileName = m.getArchiver().getPath().getFileName().toString();
            fileName = com.google.common.io.Files.getNameWithoutExtension(fileName);

            BBFileDialog dialog = new BBFileDialog(
                    m.getWpManager().getShell(),
                    SWT.SAVE,
                    fileName,
                    Format.fileDialogNames(),
                    Format.fileDialogExtensions(),
                    ArrayUtils.indexOf(Format.values(), selectedFormat)
            );

            String filename = dialog.open();
            if (filename == null) {
                return null;
            }
            Format format = Format.matchExtension(filename, dialog.getWidget().getFilterIndex());
            return Pair.of(format, filename);
        }
    }

    public static String volumeToBRF(UTDTranslationEngine engine, Document doc, int volume, boolean convertLineEndings) throws IOException {
        log.trace("Saving volume {}", volume);
        List<Element> volumeEndBrls = FastXPath.descendantFindList(doc, (results, curNode) -> {
            if (!BBX.BLOCK.VOLUME_END.isA(curNode)) {
                return false;
            }
            List<Element> blockBrls = FastXPath.descendantFindList(
                    curNode,
                    (brlResults, curEndBlockChild) -> UTDElements.BRL.isA(curEndBlockChild)
            );
            //manually add the results we want instead of this method doing it for us
            results.add(Iterables.getLast(blockBrls));
            return false;
        });

        final StringBuilder brfBuilder = new StringBuilder();
        MutableObject<String> finalBrfMut = new MutableObject<>();

        final BRFWriter.OutputCharStream stream = convertLineEndings ? BRFWriter.lineEndingRewriter(brfBuilder::append) : brfBuilder::append;

        engine.toBRF(doc, stream, 0, new BRFWriter.PageListener() {
            State saveState = State.NOT_RUNNING;

            {
                if (volume == 0) {
                    saveState = State.STARTED;
                }
            }

            @Override
            public void onBeforeBrl(BRFWriter grid, Element brl) {
                if (saveState == State.FINISHED) {
                    return;
                }

                //Note: As this is called before brls are processed triggers are nessesary
                if (volume == 0) {
                    //Save everything from the start of toBRF to the first volume end brl
                    if (finalBrfMut.getValue() == null && brl == volumeEndBrls.get(0)) {
                        //Haven't processed last brl yet
                        saveState = State.END_TRIGGER;
                    }

                } else if (saveState == State.NOT_RUNNING && brl == volumeEndBrls.get(volume - 1)) {
                    saveState = State.START_TRIGGER;
                } else if (saveState == State.STARTED && volume != volumeEndBrls.size() && brl == volumeEndBrls.get(volume)) {
                    saveState = State.END_TRIGGER;
                }

                log.debug("State {} element {}", saveState, brl.toXML());
            }

            @Override
            public void onAfterFlush(BRFWriter brfWriter) {
                if (saveState == State.FINISHED) {
                    return;
                }

                if (saveState == State.START_TRIGGER) {
                    log.debug("Start triggered");
                    brfBuilder.setLength(0);
                    saveState = State.STARTED;
                } else if (saveState == State.END_TRIGGER) {
                    finalBrfMut.setValue(brfBuilder.toString());
                    saveState = State.FINISHED;
                }
            }
        }, true);

        //The last volume does not end with a END OF VOLUME tag
        if (volume == volumeEndBrls.size()) {
            if (finalBrfMut.getValue() != null) {
                throw new IllegalStateException("Wut " + finalBrfMut.getValue());
            }
            finalBrfMut.setValue(brfBuilder.toString());
        }

        String finalBrf = finalBrfMut.getValue();
        if (StringUtils.isBlank(finalBrf)) {
            throw new IllegalStateException("No finalBrf?! " + System.lineSeparator() + finalBrf + System.lineSeparator() + "=------=");
        }

        //Remove lingering empty page from resetting the page to restart the BRF output
        int linesPerPage = engine.getBrailleSettings().getCellType().getLinesForHeight(
            BigDecimal.valueOf(engine.getPageSettings().getDrawableHeight()));
        if (finalBrf.startsWith(StringUtils.repeat(BRFWriter.NEWLINE, linesPerPage) + BRFWriter.PAGE_SEPARATOR)) {
            finalBrf = finalBrf.substring(linesPerPage + 1);
        }

        return finalBrf;
    }

    public enum State {
        NOT_RUNNING,
        START_TRIGGER,
        STARTED,
        END_TRIGGER,
        FINISHED,
    }

    public enum Format {
        BRF("brf", "Braille Ready File"),
        PEF("pef", "Portable Embosser Format");

        private final String extension;
        private final String name;

        Format(String extension, String name) {
            this.extension = extension;
            this.name = name;
        }

        public static String[] fileDialogExtensions() {
            String[] result = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                Format value = values()[i];
                result[i] = "*." + value.extension;
            }
            return result;
        }

        public static String[] fileDialogNames() {
            String[] result = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                Format value = values()[i];
                result[i] = value.name + " (*." + value.extension + ")";
            }
            return result;
        }

        public static Format matchExtension(@Nonnull String filename, int fallbackEnumIndex) {
            Format result = values()[fallbackEnumIndex];
            int periodIndex = filename.lastIndexOf('.');
            if (periodIndex == -1) {
                log.warn("missing extension {}", filename);
                log.warn("falling back to value {}", result);
            } else {
                String fileExtension = filename.substring(periodIndex + 1);
                for (Format value : values()) {
                    if (value.extension.equals(fileExtension)) {
                        return value;
                    }
                }
                log.warn("invalid extension on {}", filename);
            }
            if (result == null) {
                throw new RuntimeException("null");
            }
            return result;
        }
    }
}
