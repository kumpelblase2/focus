package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.FilenameConstants.CAPABILITY_FILE_NAME
import de.eternalwings.focus.storage.FilenameConstants.CLIENT_FILE_DATE_FORMAT
import de.eternalwings.focus.storage.FilenameConstants.CLIENT_FILE_NAME
import de.eternalwings.focus.storage.FilenameConstants.CONTENT_FILE_NAME
import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.ChangesetFile
import de.eternalwings.focus.storage.data.OmniContainer
import de.eternalwings.focus.storage.data.xml.OmniContainerXmlConverter
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.Plist
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.streams.toList

open class NormalStorage(override val location: Path) : PhysicalOmniStorage {
    private val fileChangesets: List<Changeset> by lazy {
        changeSetFiles.stream()
            .map(this::createChangesetForFile)
            .collect(Collectors.toList())
    }

    override val changeSets: List<Changeset>
        get() = (fileChangesets + transientChangesets).sortedBy { it.timestamp }

    private var transientChangesets: List<Changeset> = emptyList()

    private val OmniDevice.file: Path
        get() = location.resolve(fileName)

    private val OmniDevice.fileName: String
        get() {
            return CLIENT_FILE_DATE_FORMAT.format(this.lastSync) + "=" + this.clientId + CLIENT_FILE_NAME
        }

    private val lastChangesetId: String
        get() = changeSetFiles.asSequence().maxBy { it.timestamp }!!.id

    override fun removeDevice(clientId: String) {
        val deviceEntries = devices.filter { it.clientId == clientId }
        deviceEntries.forEach { removeDeviceChangeset(it) }
    }

    private fun removeDeviceChangeset(device: OmniDevice) {
        Files.delete(device.file)
    }

    override fun updateDevice(device: OmniDevice, refreshLastSync: Boolean) {
        val updatedDevice = if (refreshLastSync) {
            device.copy(tailIds = listOf(lastChangesetId), lastSync = OffsetDateTime.now())
        } else {
            device
        }
        val deviceId = updatedDevice.clientId
        Plist.writePlist(updatedDevice.toPlist(), updatedDevice.file)

        val deviceChangesets = devices.filter { it.clientId == deviceId }
        if (deviceChangesets.count() > 3) {
            // Only persist the most recent 3 changesets
            val sorted = deviceChangesets.sortedByDescending { it.lastSync }
            for (i in 3 until sorted.size) {
                removeDeviceChangeset(sorted[i])
            }
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
        return OmniContainerXmlConverter.read(xmlContent)
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

    override fun appendChangeset(changeset: Changeset, persist: Boolean) {
        if (persist) {
            this.save(changeset)
        } else {
            transientChangesets = transientChangesets + changeset
        }
    }

    override fun save(changeset: Changeset) {
        val output = XMLOutputter(Format.getPrettyFormat())
        val xmlDocument = OmniContainerXmlConverter.write(changeset.container)
        val filename = changeset.createFilename()
        val byteOutput = ByteArrayOutputStream()
        ZipOutputStream(byteOutput).use {
            it.putNextEntry(ZipEntry(CONTENT_FILE_NAME))
            output.output(xmlDocument, it.writer())
            it.closeEntry()
        }
        createChangesetFile(filename, byteOutput.toByteArray())
    }

    protected open fun createChangesetFile(filename: String, output: ByteArray) {
        Files.write(
            location.resolve(filename),
            output,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE
        )
    }

    companion object {
        private val xmlBuilder = SAXBuilder()
    }
}
