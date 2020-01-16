package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.OmniContainer
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.Plist
import de.eternalwings.focus.storage.xml.parseXml
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.zip.ZipInputStream
import kotlin.streams.toList

open class NormalStorage(override val location: Path) : OmniStorage {
    override val changeSets: List<Changeset> by lazy {
        changesetFiles.parallelStream()
            .map(this::createChangesetForFile)
            .sorted(Comparator.comparing(Changeset::timestamp))
            .collect(Collectors.toList())
    }

    private val changesetFiles: Collection<Path>
        get() {
            return Files.list(location)
                .filter { Files.isRegularFile(it) }
                .filter { CHANGESET_FILE_REGEX.matches(it.fileName.toString()) }
                .toList()
        }

    protected fun createChangesetForFile(file: Path): Changeset {
        val filenameMatch = CHANGESET_FILE_REGEX.matchEntire(file.fileName.toString()) ?: throw IllegalStateException()
        val timestamp = filenameMatch.groupValues[1].toLong()
        val previousId = filenameMatch.groupValues[2]
        val id = filenameMatch.groupValues[3]
        getContentOfFile(file).use {
            return Changeset(timestamp, id, previousId, parseFile(it))
        }
    }

    private fun parseFile(zipInputStream: ZipInputStream): OmniContainer {
        val next = zipInputStream.nextEntry
        if (!next.name!!.contentEquals(CONTENT_FILE_NAME)) {
            throw IllegalStateException("Got non-content file in zip")
        }
        val content = zipInputStream.readBytes()
        val inputStreamFromBytes = ByteArrayInputStream(content)
        return parseXml(inputStreamFromBytes)
    }

    protected open fun getContentOfFile(file: Path): ZipInputStream {
        return ZipInputStream(FileInputStream(file.toFile()))
    }

    override val devices: Collection<OmniDevice>
        get() {
            return Files.list(location)
                .filter { it.fileName.toString().endsWith(CLIENT_FILE_NAME) }
                .map { Plist.parsePlist(it) }
                .map { OmniDevice.fromPlist(it as DictionaryObject) }
                .toList()
        }

    override val capabilities: Collection<OmniCapability>
        get() {
            return Files.list(location)
                .filter { it.fileName.toString().endsWith(CAPABILITY_FILE_NAME) }
                .map { Plist.parsePlist(it) }
                .map { OmniCapability.fromPlist(it as DictionaryObject) }
                .toList()
        }

    companion object {
        val CHANGESET_FILE_REGEX = "(\\d{14})=(.{11})\\+(.{11})\\.zip$".toRegex()
        const val CONTENT_FILE_NAME = "contents.xml"
        const val CLIENT_FILE_NAME = ".client"
        const val CAPABILITY_FILE_NAME = ".capability"
    }
}
