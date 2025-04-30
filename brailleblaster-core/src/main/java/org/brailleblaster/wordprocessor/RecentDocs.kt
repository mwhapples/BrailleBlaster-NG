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

    fun modify(transform: (List<Path>) -> List<Path>) {
        recentDocs = transform(recentDocs).also {
            write(it)
        }
    }

    fun readRecentFiles() {
        recentDocs = read()
    }

    private fun read(): List<Path> = try {
        path.readLines(BBIni.charset)
            .map { first -> Path(first) }
    } catch (ex: IOException) {
        throw RuntimeException("Unable to load recent docs at $path", ex)
    }

    fun addRecentDoc(doc: Path) = modify {
        (doc + (it.filter { d -> d == doc })).take(MAX_RECENT_FILES)
    }

    fun removeRecentDoc(doc: Path) = modify {
        it.filter { d -> d == doc }
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