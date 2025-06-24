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
package org.brailleblaster.archiver2;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import nu.xom.Document;
import org.brailleblaster.archiver2.ArchiverFactory.FileLoader;
import org.brailleblaster.utd.exceptions.NodeException;
import org.brailleblaster.utd.internal.xml.XMLHandler;

/**
 * Open and convert nimas books to BBX
 */
public class NimasZipArchiverLoader implements FileLoader {
    public static final NimasZipArchiverLoader INSTANCE = new NimasZipArchiverLoader();
    private static final Logger log = LoggerFactory.getLogger(NimasZipArchiverLoader.class);

    @NotNull
    @Override
    public ImmutableMap<String, String> getExtensionsAndDescription() {
        return ImmutableMap.of(
                "*.zip", "Nimas ZIP (*.zip)"
        );
    }

    @Override
    public Archiver2 tryLoad(@NotNull Path zipFile, @NotNull ArchiverFactory.ParseData fileData) throws Exception {
        FileSystem zipFS = ZipHandles.open(zipFile, false);
        //Try to find opf
        Path zipRoot = zipFS.getPath("/");

        Path bookPath = null;
        List<Path> opfFiles = OPFUtils.findOPFFilesInFolder(zipRoot);
        if (!opfFiles.isEmpty()) {
            if (opfFiles.size() > 1) {
                log.warn("Detected multiple OPF files, picking first: {}", opfFiles);
            }

            Path opfFile = opfFiles.get(0);
            log.info("Detected nimas OPF file at {}", opfFile);

            Document opfDocument;
            try {
                opfDocument = new XMLHandler().load(opfFile);
            } catch (Exception e) {
                log.error("Failed to open OPF file", e);
                opfDocument = null;
            }

            if (opfDocument != null) {
                OPFUtils.ManifestEntry bookManifest = guessNimasLocation(opfDocument);

                bookPath = opfFile.resolveSibling(bookManifest.href);

                //Issue #4693: Workaround for opf files that specify book with the wrong case
                if (!Files.exists(bookPath)) {
                    try (Stream<Path> filesStream = Files.list(bookPath.getParent())) {
                        bookPath = filesStream
                                .filter(curPath -> curPath.getFileName().toString().equalsIgnoreCase(bookManifest.href))
                                .findFirst()
                                .orElseThrow(() -> new NodeException(
                                        "Unable to find file " + bookManifest.href
                                                + " in zip root " + opfFile.getParent(), bookManifest.elem)
                                );
                    }
                }
            }
        }

        if (bookPath == null) {
            log.warn("No opf files detected, brute forcing");
            try (Stream<Path> filesStream = Files.walk(zipRoot)) {
            bookPath = filesStream
                    .filter(OPFUtils::pathNotHiddenOrInHiddenDirectory)
                    .filter(curPath -> curPath.getFileName().toString().endsWith(".xml"))
                    .filter(curPath -> {
                        try {
                            return new XMLHandler().load(curPath).getRootElement().getLocalName().equals("dtbook");
                        } catch (Exception e) {
                            log.warn("Failed to detect xml root element of {}", curPath, e);
                            return false;
                        }
                    }).findFirst()
                    .orElse(null);}
            if (bookPath == null) {
                log.warn("File is not a nimas zip: {}", zipFile);
                return null;
            }
        }

        log.debug("book Path {}", bookPath);
        Document convertedDoc = FileLoader.Companion.convert(bookPath, "nimas");
        return new BBZArchiver(
                zipFile,
                zipFS,
                bookPath.resolveSibling(bookPath.getFileName().toString() + ".bbx"),
                convertedDoc
        );
    }

    /**
     * The reality of the OPF "standard"
     * - Every book has a different id
     * - Every mimeType you can think of for XML is used
     * - application/dtbook+html
     * - application/x-dtbook+xml
     * - application/dtbook+xml
     * - text/xml
     * - xml/document
     * - And presumably others
     * - The mimeType's are the same for the OPF and XML book
     * <p>
     * So we must guess and hope it's correct
     */
    public static OPFUtils.ManifestEntry guessNimasLocation(Document opfDocument) {
        String opfFormat = OPFUtils.getDCElementValueCaseInsensitive(opfDocument, "format");
        //TODO: When epub support is added this should be more explicit
        if (!StringUtils.containsIgnoreCase(opfFormat, "nimas")
                //Standard name of DTD 2002
                || !opfFormat.equals("ANSI/NISO Z39.86-2002")) {
            log.warn("OPF at {} does not explicitly state nimas, says {}, assuming",
                    opfDocument.getBaseURI(),
                    opfFormat
            );
        }

        //Find first XML file, most likely the nimas book
        for (OPFUtils.ManifestEntry curManifestEntry : OPFUtils.getManifestItems(opfDocument)) {
            try {
                if (curManifestEntry.href.endsWith(".xml")) {
                    return curManifestEntry;
                }
            } catch (Exception e) {
                throw new NodeException("Mangled manifestEntry", curManifestEntry.elem, e);
            }
        }

        throw new NodeException("No usable book entry found", opfDocument);
    }
}
