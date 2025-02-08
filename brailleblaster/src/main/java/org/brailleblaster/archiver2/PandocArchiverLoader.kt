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
package org.brailleblaster.archiver2

import com.google.common.collect.ImmutableMap
import com.sun.jna.Platform
import org.brailleblaster.BBIni
import org.brailleblaster.pandoc.FixImage
import org.brailleblaster.pandoc.FixMathML
import org.brailleblaster.pandoc.FixNestedList
import org.brailleblaster.pandoc.Fixer
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object PandocArchiverLoader : ArchiverFactory.FileLoader {
    // private var fileTabName = ""
    @Throws(Exception::class)
    override fun tryLoad(file: Path, fileData: ArchiverFactory.ParseData): Archiver2 {
        val archiver: Archiver2
        val fromFormat: String? = when (fileData.type) {
            ArchiverFactory.Types.DOCX -> "docx+styles+empty_paragraphs"
            ArchiverFactory.Types.EPUB -> "epub"
            ArchiverFactory.Types.HTML -> "html+empty_paragraphs"
            ArchiverFactory.Types.MD -> "markdown"
            ArchiverFactory.Types.ODT -> "odt"
            ArchiverFactory.Types.TEX -> "latex"
            else -> null
        }

        // run pandoc
        val (bbxFile, fileTabName) = pandocImport(file.toString(), fromFormat)
        val newFilePath = FileSystems.getDefault().getPath(bbxFile)

        // attempt to load file
        archiver = ArchiverFactory.INSTANCE.load(newFilePath)
        // set new file name to be set in window tab
        archiver.newPath = Paths.get(fileTabName)
        // Set where the document was really imported from, not the temp bbx.
        archiver.importedFrom = file
        return archiver
    }

    override val extensionsAndDescription: ImmutableMap<String, String>
        get() {
        return ImmutableMap.Builder<String, String>()
            .put("*.docx", "Microsoft Word Files (*.docx)")
            .put("*.epub", "Epub Books (*.epub)")
            .put("*.htm", "HTML Files (*.htm)")
            .put("*.html", "HTML Files (*.html)")
            .put("*.xhtml;*.xhtm;*.xht", "XHTML Files (*.xhtml;*.xhtm;*.xht)")
            .put("*.md", "Markdown Files (*.md)")
            .put("*.odt", "Open Document Files (*.odt)")
            .put("*.tex", "LaTeX files (*.tex)")
            .build()
    }

    @Throws(Exception::class)
    private fun pandocImport(filename: String, fromFormat: String?): Pair<String, String> {
        val wrkDir: File
        val outFilename: String
        val env = arrayOfNulls<String>(1)

        // check the file encoding and convert if necessary
        checkFileEncoding(filename)

        // construct new filename with bbx extension
        var newFilename: String
        val dot = filename.lastIndexOf('.')
        newFilename = if (dot > -1) {
            filename.substring(0, dot)
        } else {
            filename
        }
        outFilename = newFilename
        val fileTabName = "$newFilename.bbz"
        newFilename = "$newFilename-"

        // determine the os and executable to use
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())
        val cmd = when {
            os.contains("win") -> {
                if (Platform.is64Bit()) PANDOCWIN_X64 else PANDOCWIN_X86
            }
            os.contains("mac") -> {
                PANDOCMAC
            }
            os.contains("linux") -> {
                    val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())
if                     (arch.contains("aarch64")){
                                    PANDOCLINUX_AARCH64
                    }else{
                PANDOCLINUX_X64
                }
            }
            else -> null
        }

        // set the working dir and execute
        try {
            wrkDir = File(PANDOCLUA)
            env[0] = "PANDOCCMD=$cmd"
            val outFile = File.createTempFile("bb-$outFilename-pandoc-err-", ".txt")
            outFile.deleteOnExit()
            val bbFile = File.createTempFile(newFilename, ".bbx")
            bbFile.deleteOnExit()
            newFilename = bbFile.absolutePath
            val pb = ProcessBuilder(
                cmd, "--from=$fromFormat",
                "--to=bbx.lua",
                "--output=" + bbFile.absolutePath,
                filename
            )
                .directory(wrkDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectErrorStream(true)
                .redirectOutput(outFile)
            pb.environment()["PANDOCCMD"] = cmd
            var logStr = "\n***** PandocArchiverLoader *****\n"
            logStr = """
                $logStr${pb.command()}
                
                """.trimIndent()
            logStr = """
                ${logStr}input file:$filename
                output file:$newFilename
                
                """.trimIndent()
            logStr = "$logStr********************************\n"
            log.debug(logStr)
            val proc = pb.start()
            val status = proc.waitFor()
            log.debug("***** pandoc done *****\n")
            //  if the status is not 0 then make sure the process is gone
            if (status != 0) {
                log.error("pandocImport: unable to import file:$filename, pandoc gave exit code $status")
                proc.destroyForcibly()
                throw Exception("pandocImport: unable to import file:$filename")
            }
            // run fixers on the document
            val fixNestedList = FixNestedList()
            val fixImage = FixImage()
            val fixMath = FixMathML()
            val fixer = Fixer(newFilename)
            fixer.addFixer(fixMath)
            fixer.addFixer(fixNestedList)
            fixer.addFixer(fixImage)
            fixer.processFixers()
        } catch (ex: Exception) {
            val strWriter = StringWriter()
            val prWriter = PrintWriter(strWriter)
            ex.printStackTrace(prWriter)
            log.debug("\n***** PandocArchiverLoader *****\n")
            log.debug(strWriter.toString())
            log.debug("\n********************************\n")
            prWriter.close()
            throw ex
        }
        return newFilename to fileTabName
    }

    // method for checking the encoding of a file and resaving it to have
    // UTF-8 encoding if necessary. 
    @Throws(Exception::class)
    private fun checkFileEncoding(filename: String): String {
        var fname = filename
        val sb = StringBuilder()
        val buffer = CharArray(4096)
        val fos: FileOutputStream
        val osw: OutputStreamWriter
        var fis = FileInputStream(fname)
        var isr = InputStreamReader(fis)
        val defaultEncoding = isr.encoding
        isr.close()
        // if the encoding is not UTF-8 then resave the file
        if (!defaultEncoding.equals("utf-8", ignoreCase = true) &&
            !filename.endsWith(".epub")
        ) {
            fis = FileInputStream(filename)
            isr = InputStreamReader(fis, StandardCharsets.UTF_8)
            var stat = 0
            while (isr.ready() && -1 < stat) {
                stat = isr.read(buffer, 0, 4096)
                if (stat > 0) sb.appendRange(buffer, 0, stat)
            }
            isr.close()
            var prefix = "bbtemp-"
            var suffix: String? = ".txt"
            val dot = filename.lastIndexOf(".")
            if (-1 < dot) {
                suffix = filename.substring(dot)
                prefix = filename.substring(0, dot) + "-"
            }
            // make a temporary file for pandoc to use
            val tmpFile = File.createTempFile(prefix, suffix)
            tmpFile.deleteOnExit()
            fos = FileOutputStream(tmpFile)
            osw = OutputStreamWriter(fos, StandardCharsets.UTF_8)
            osw.write(sb.toString())
            osw.close()
            fname = tmpFile.absolutePath
        }
        return fname
    }

    private val PANDOCDIR = BBIni.programDataPath.resolve("pandoc").toString()
    private val PANDOCWIN_X64 = "$PANDOCDIR/win-x64/pandoc.exe"
    private val PANDOCWIN_X86 = "$PANDOCDIR/win-x86/pandoc.exe"
    private val PANDOCMAC = "$PANDOCDIR/mac-x64/pandoc"
    private val PANDOCLINUX_X64 = "$PANDOCDIR/linux-x64/pandoc"
        private val PANDOCLINUX_AARCH64 = "$PANDOCDIR/linux-AARCH64/pandoc"
    private val PANDOCLUA = "$PANDOCDIR/lua"
    private val log = LoggerFactory.getLogger(PandocArchiverLoader::class.java)
}
