package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.FilenameConstants.CAPABILITY_FILE_NAME
import de.eternalwings.focus.storage.FilenameConstants.CLIENT_FILE_DATE_FORMAT
import de.eternalwings.focus.storage.FilenameConstants.CLIENT_FILE_NAME
import de.eternalwings.focus.storage.FilenameConstants.CONTENT_FILE_NAME
import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.ChangesetFile
import de.eternalwings.focus.storage.data.OmniContainer
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.Plist
import org.jdom2.input.SAXBuilder
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.zip.ZipInputStream
import kotlin.streams.toList

open class NormalStorage(override val location: Path) : OmniStorage, PhysicalStorage {
    override val changeSets: List<Changeset> by lazy {
        changeSetFiles.parallelStream()
            .map(this::createChangesetForFile)
            .sorted(Comparator.comparing(Changeset::timestamp))
            .collect(Collectors.toList())
    }

    override fun registerDevice(device: OmniDevice) {
        val deviceId = device.clientId
        val date = ZonedDateTime.now()
        val dateString = date.format(CLIENT_FILE_DATE_FORMAT)
        val path = location.resolve("$dateString=$deviceId.client")
        val lastChangeset = changeSetFiles.last()
        Plist.writePlist(device.copy(tailIds = listOf(lastChangeset.id)).toPlist(), path)
    }

    override fun removeDevice(clientId: String) {
        val deviceEntries = devices.filter { it.clientId == clientId }
        deviceEntries.forEach { device ->
            val filename = CLIENT_FILE_DATE_FORMAT.format(device.lastSync) + "=" + device.clientId + CLIENT_FILE_NAME
            Files.delete(location.resolve(filename))
        }
    }

    override val changeSetFiles: List<ChangesetFile>
        get() {
            return Files.list(location)
                .filter { Files.isRegularFile(it) }
                .flatMap {
                    val file = ChangesetFile.fromFile(it)
                    if (file == null) Stream.empty()
                    else Stream.of(file)
                }
                .sorted(Comparator.comparing(ChangesetFile::timestamp))
                .toList()
        }

    protected open fun createChangesetForFile(file: ChangesetFile): Changeset {
        val content = getContentOfFile(file)
        return Changeset(file.timestamp, file.id, file.previousId, parseFile(content))
    }

    private fun parseFile(content: ByteArray): OmniContainer {
        val inputStreamFromBytes = ByteArrayInputStream(content)
        val xmlContent = xmlBuilder.build(inputStreamFromBytes)
        return OmniContainer.fromXML(xmlContent)
    }

    override fun getContentOfFile(file: ChangesetFile): ByteArray {
        getContentOfFile(file.path).use {
            val next = it.nextEntry
            if (!next.name!!.contentEquals(CONTENT_FILE_NAME)) {
                throw IllegalStateException("Got non-content file in zip")
            }
            return it.readBytes()
        }
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
        private val xmlBuilder = SAXBuilder()
    }
}
