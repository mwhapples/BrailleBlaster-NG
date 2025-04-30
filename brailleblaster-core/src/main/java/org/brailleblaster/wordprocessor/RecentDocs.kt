package org.brailleblaster.wordprocessor

import org.brailleblaster.BBIni
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.writeLines

class RecentDocs(val path: Path) {
    var recentDocs: List<Path> = read()
        private set
    fun readRecentFiles() {
        recentDocs = read()
    }

    private fun read(): List<Path> = try {
        path.readLines(BBIni.charset)
            .map { first -> Path(first) }
    } catch (ex: IOException) {
        throw RuntimeException("Unable to load recent docs at $path", ex)
    }

    fun addRecentDoc(doc: Path) {
        recentDocs = (doc + (recentDocs.filter { it == doc })).take(MAX_RECENT_FILES).also {
            write(it)
        }
    }

    fun writeRecentFiles() = write(recentDocs)
    private fun write(files: List<Path>) {
        try {
            path.writeLines(
                files.map { curPath: Path -> curPath.toAbsolutePath().toString() },
                charset = BBIni.charset
            )
        } catch (e: IOException) {
            throw RuntimeException("Unable to save recent docs file", e)
        }
    }
    companion object {
        const val MAX_RECENT_FILES = 20
        val defaultRecentDocs = RecentDocs(BBIni.recentDocs)
    }
}