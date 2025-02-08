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
package org.brailleblaster.perspectives.mvc.modules.misc

import com.google.common.collect.ImmutableSet
import org.brailleblaster.BBIni
import org.brailleblaster.archiver2.Archiver2
import org.brailleblaster.archiver2.ArchiverFactory
import org.brailleblaster.archiver2.ArchiverRecoverThread
import org.brailleblaster.archiver2.BBZArchiver.BBZSaveOptions
import org.brailleblaster.archiver2.SaveOptions
import org.brailleblaster.perspectives.braille.Manager
import org.brailleblaster.perspectives.mvc.BBSimpleManager.SimpleListener
import org.brailleblaster.perspectives.mvc.SimpleEvent
import org.brailleblaster.perspectives.mvc.events.BuildMenuEvent
import org.brailleblaster.perspectives.mvc.menu.MenuManager.addMenuItem
import org.brailleblaster.perspectives.mvc.menu.MenuManager.addSubMenu
import org.brailleblaster.perspectives.mvc.menu.SubMenuBuilder
import org.brailleblaster.perspectives.mvc.menu.TopMenu
import org.brailleblaster.tools.*
import org.brailleblaster.wordprocessor.BBFileDialog
import org.brailleblaster.wordprocessor.WPManager
import org.eclipse.swt.SWT
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer
import java.util.stream.Collectors

class FileModule : SimpleListener {
    override fun onEvent(event: SimpleEvent) {
        if (event is BuildMenuEvent) {
            addMenuItem(NewFileTool)
            addMenuItem(OpenFileTool)
            addSubMenu(buildSubMenu())
            addSubMenu(buildSubMenuAutoSave())
            //addMenuItem(RecentAutoSavesTool)
            addMenuItem(SaveTool)
            addMenuItem(SaveAsTool)
            addMenuItem(SaveVolumeBrfPefTool)
            addMenuItem(PrintTool)
            addMenuItem(EmbossTool)
            addMenuItem(BraillePreviewTool)
            // Disable split and merge as not being used.
//			MenuManager.addMenuItem(
//					MenuManager.TopMenu.FILE,
//					"Split",
//					0,
//					e -> {
//						e.manager.splitBook();
//					},
//					null);
//
//			MenuManager.addMenuItem(
//					MenuManager.TopMenu.FILE,
//					"Merge",
//					0,
//					e -> {
//						e.manager.mergeBook();
//					},
//					null);
            addMenuItem(CloseTool)
            addMenuItem(ExitTool)
        }
    }

    private fun buildSubMenu(): SubMenuBuilder {
        val smb = SubMenuBuilder(
            TopMenu.FILE,
            "Recent Document"
        )
        val recentDocs: List<Path> = readRecentFiles()
        recentDocs.forEach(Consumer { curPath: Path ->
            val fileName = curPath.fileName.toString()
            val itemText = "$fileName  [$curPath]"
            if (Files.exists(curPath) && !Files.isDirectory(curPath)) {
                smb.addItem(itemText, 0) { WPManager.getInstance().addDocumentManager(curPath) }
            }
        })
        return smb
    }

    private fun buildSubMenuAutoSave(): SubMenuBuilder {
        val smb = SubMenuBuilder(
            TopMenu.FILE,
            "Recent Auto Saves"
        )
        val recentSaves = ArchiverRecoverThread.recentSaves
        recentSaves.forEach(Consumer { curPath: Path ->
            val fileName = curPath.fileName.toString()
            val itemText = "$fileName  [$curPath]"
            if (Files.exists(curPath) && !Files.isDirectory(curPath)) {
                smb.addItem(itemText, 0) { WPManager.getInstance().addDocumentManager(curPath) }
            }
        })
        return smb
    }

    companion object {
        private val log = LoggerFactory.getLogger(FileModule::class.java)
        private const val MAX_RECENT_FILES = 20

        fun fileSave(m: Manager): Boolean {
            log.debug("Saving file")
            m.checkForUpdatedViews()
            m.waitForFormatting(true)
            val arch = m.archiver
            val pathToRemove = arch.path.fileName.toString()
            val isNewFile = arch.path == Manager.DEFAULT_FILE
            val isImported = arch.isImported
            val isRecoveryFile = arch.path.toString().contains(BBIni.autoSavePath.toString())
            val isNotWritable = !arch.path.toFile().canWrite()
            log.debug(
                "isNewFile {} isImported {} isRecoveryFile {} isNotWritable {}",
                isNewFile,
                isImported,
                isRecoveryFile,
                isNotWritable
            )
            return if (isNewFile || isImported || isRecoveryFile || isNotWritable) {
                val success = saveAs(m)
                if (success) {
                    log.error("Setting as not imported")
                    arch.setNotImported()
                }
                log.debug("Saved file")
                success
            } else {
                save(m, arch, arch.path)
                ArchiverRecoverThread.removeFile(pathToRemove)
                log.debug("Saved file")
                true
            }
        }
        /**
         * Open the Save As dialog
         * @return false if Cancel was pressed
         */
        fun saveAs(m: Manager): Boolean {
            log.debug("Saving as")
            m.waitForFormatting(true)
            val arch = m.archiver
            val pathToRemove = arch.path.fileName.toString()
            val filePath: String?
            if (!BBIni.debugging) {
                var fileName: String = (arch.newPath ?: arch.path).fileName.toString()
                fileName = com.google.common.io.Files.getNameWithoutExtension(fileName)
                val dialog = BBFileDialog(
                    m.wpManager.shell,
                    SWT.SAVE,
                    fileName,
                    ArchiverFactory.INSTANCE.getSupportedDescriptions(arch),
                    ArchiverFactory.INSTANCE.getSupportedExtensions(arch)
                )
                filePath = dialog.open()
            } else {
                filePath = BBIni.debugSavePath.toString()
            }
            if (filePath == null) {
                //user pressed cancel
                log.debug("User cancelled save as")
                return false
            }
            save(m, arch, Paths.get(filePath))
            ArchiverRecoverThread.removeFile(pathToRemove)
            arch.setNotImported()
            log.debug("File saved")
            return true
        }

        private fun save(m: Manager, arch: Archiver2, filePath: Path) {
            m.checkForUpdatedViews()
            val engine = m.document.engine
            if (filePath == arch.path) {
                arch.save(
                    filePath,
                    arch.bbxDocument,
                    engine,
                    ImmutableSet.of<SaveOptions>(BBZSaveOptions.IncludeBRF, BBZSaveOptions.IncludePEF)
                )
            } else {
                arch.saveAs(
                    filePath,
                    arch.bbxDocument,
                    engine,
                    ImmutableSet.of<SaveOptions>(BBZSaveOptions.IncludeBRF, BBZSaveOptions.IncludePEF)
                )
                m.setTabTitle(filePath.fileName.toString())
                addRecentDoc(arch.path)
            }
            m.text.hasChanged = false
            m.braille.hasChanged = false
            m.isDocumentEdited = false
        }

        fun readRecentFiles(): MutableList<Path> {
            return try {
                Files.readAllLines(BBIni.recentDocs, BBIni.charset)
                    .map { first -> Paths.get(first) }.toMutableList()
            } catch (ex: IOException) {
                throw RuntimeException("Unable to load recent docs at " + BBIni.recentDocs, ex)
            }
        }

        @JvmStatic
        fun addRecentDoc(path: Path) {
            val recentDocs = readRecentFiles()
            recentDocs.remove(path)
            while (recentDocs.size >= MAX_RECENT_FILES) {
                recentDocs.removeAt(recentDocs.size - 1)
            }
            recentDocs.add(0, path)
            try {
                Files.write(
                    BBIni.recentDocs,
                    recentDocs.stream().map { curPath: Path -> curPath.toAbsolutePath().toString() }
                        .collect(Collectors.toList())
                )
            } catch (e: IOException) {
                throw RuntimeException("Unable to save recent docs file", e)
            }
        }
    }
}