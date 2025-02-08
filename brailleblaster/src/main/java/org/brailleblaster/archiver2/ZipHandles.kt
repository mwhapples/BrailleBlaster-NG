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

import org.brailleblaster.BBIni
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

/**
 * Manage open file handles, handling edge cases of a zip file opened multiple times
 */
object ZipHandles {
  private val log = LoggerFactory.getLogger(ZipHandles::class.java)
  private val HANDLES: MutableMap<Path, Data> = HashMap()

  /**
   * Get a FileSystem handle. DO NOT close() or use with try-with-resources
   * or you will get ClosedFileSystemException.
   * @param rawPath
   * @param create
   * @return
   */
  @JvmStatic
  fun open(rawPath: Path, create: Boolean): FileSystem {
    if (Files.exists(rawPath) && rawPath.toFile().length() == 0L) {
      // broken zip, newZipFileSystem will likely fail
      try {
        Files.delete(rawPath)
      } catch (e: Exception) {
        throw RuntimeException("Failed to delete old empty file at $rawPath", e)
      }
    }
    if (!Files.exists(rawPath)) {
      val zipTemplate = BBIni.programDataPath.resolve("emptyZipTemplate.zip")
      if (!Files.exists(zipTemplate)) {
        try {
          val templateFS = newZipFileSystem(zipTemplate, true)
          templateFS.close()
        } catch (e: Exception) {
          throw RuntimeException("Failed to create $zipTemplate", e)
        }
      }
      try {
        Files.copy(zipTemplate, rawPath)
      } catch (ex: Exception) {
        throw RuntimeException("Failed to create $rawPath", ex)
      }
    }
    val path = toRealPath(rawPath)
    log.debug("Mapped {} to {}", rawPath, path)
    return HANDLES.getOrPut(path) { Data(newZipFileSystem(path, create)) }.apply { instances++ }.fs
  }

  @JvmStatic
  fun close(rawPath: Path) {
    val path = toRealPath(rawPath)
    val data: Data? = HANDLES[path]
    log.debug("closing {}", rawPath)
    if (data == null) {
      //Not a terribly helpful exception for the user. Just log it and return.
      log.error("Cannot find file system for $path")
      return
    }
    else if (data.instances == 1) {
      HANDLES.remove(path)
      try {
        data.fs.close()
      }
      catch (ex: IOException) {
        throw RuntimeException("Could not close FS " + data.fs + " for " + path, ex)
      }
    } else {
      data.instances--
    }
  }

  fun closeAll() {
    HANDLES.values.forEach { data: Data? ->
      if (data != null) {
        try {
          data.fs.close()
        } catch (e: IOException) {
          log.warn("Exception whilst closing handle", e)
        }
      }
    }
    HANDLES.clear()
  }

  @JvmStatic
  fun has(rawPath: Path): Boolean {
    return if (!Files.exists(rawPath)) {
      check(!HANDLES.containsKey(rawPath)) { "File no longer exists $rawPath" }
      check(!HANDLES.containsKey(rawPath.toAbsolutePath())) { "File no longer exists " + rawPath.toAbsolutePath() }
      false
    } else {
      val path = toRealPath(rawPath)
      HANDLES.containsKey(path)
    }
  }

  private fun toRealPath(rawPath: Path): Path {
    return try {
      rawPath.toRealPath()
    } catch (e: Exception) {
      throw RuntimeException("Failed to resolve path $rawPath", e)
    }
  }

  private fun newZipFileSystem(zipFile: Path, create: Boolean): FileSystem {
    return try {
      val attrs = Files.readAttributes(zipFile, BasicFileAttributes::class.java)
      log.debug(
        "Getting file system for {}, is regular file {} is symbolic link {}, is directory {}, is other {}",
        zipFile,
        attrs.isRegularFile,
        attrs.isSymbolicLink,
        attrs.isDirectory,
        attrs.isOther
      )
      val realPath = if (Files.isSymbolicLink(zipFile)) Files.readSymbolicLink(zipFile) else zipFile
      FileSystems.newFileSystem(pathToZipURI(realPath), mapOf("create" to create.toString()))
    } catch (e: Throwable) {
      for ((key, value) in HANDLES) {
        log.error("{} - {}", key, value)
      }
      throw RuntimeException("Unable to make zip filesystem for $zipFile create $create", e)
    }
  }

  private fun pathToZipURI(path: Path): URI {
    val uri = path.toUri()
    return try {
      URI(
        "jar:${uri.scheme}",
        uri.userInfo,
        uri.host,
        uri.port,
        uri.path,
        uri.query,
        uri.fragment
      )
    } catch (e: Exception) {
      throw RuntimeException("Unable to make URI for $path", e)
    }
  }

  private class Data(val fs: FileSystem) {
    var instances = 0
  }
}