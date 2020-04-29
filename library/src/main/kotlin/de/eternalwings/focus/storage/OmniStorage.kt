package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.ChangesetFile
import de.eternalwings.focus.storage.plist.Plist
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface PhysicalOmniStorage : OmniStorage {
    val location: Path
    val changeSetFiles: List<ChangesetFile>

    fun getContentOfFile(file: ChangesetFile): ByteArray
}

interface OmniStorage {
    /**
     * The devices registered to this storage. This includes past versions of the same client, thus
     * this list may contain the same client multiple times.
     */
    val devices: Collection<OmniDevice>
    val uniqueDevices: Collection<OmniDevice>
        get() = devices.lastUniqueBy(OmniDevice::clientId, OmniDevice::lastSync)

    private fun <T, K> Collection<T>.lastUniqueBy(
        idProvider: (T) -> K,
        dateProvider: (T) -> OffsetDateTime
    ): Collection<T> {
        val idMap = mutableMapOf<K, T>()
        this.forEach { element ->
            idMap.merge(idProvider(element), element) { oldElement, newElement ->
                if (dateProvider(oldElement).isBefore(dateProvider(newElement))) {
                    newElement
                } else {
                    oldElement
                }
            }
        }

        return idMap.values.toSet()
    }


    val capabilities: Collection<OmniCapability>
    val changeSets: List<Changeset>

    fun registerDevice(device: OmniDevice)
    fun removeDevice(clientId: String)

    fun saveTo(location: Path) {
        check(Files.exists(location)) { "Specified location does not exist" }
        check(Files.isDirectory(location)) { "Specified location is not a directory" }

        capabilities.forEach { capability ->
            val filename = capability.name + FilenameConstants.CAPABILITY_FILE_NAME
            val resultingFile = Files.createFile(location.resolve(filename))
            Plist.writePlist(capability.toPlist(), resultingFile)
        }

        devices.forEach { device ->
            val filename =
                FilenameConstants.CLIENT_FILE_DATE_FORMAT.format(device.lastSync) + "=" + device.clientId + FilenameConstants.CLIENT_FILE_NAME
            val resultingFile = Files.createFile(location.resolve(filename))
            Plist.writePlist(device.toPlist(), resultingFile)
        }

        changeSets.forEach { changeset ->
            val output = XMLOutputter(Format.getPrettyFormat())
            val xmlDocument = changeset.container.toXML()


            val filename = changeset.createFilename()
            ZipOutputStream(
                Files.newOutputStream(
                    location.resolve(filename), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            ).use {
                it.putNextEntry(ZipEntry(FilenameConstants.CONTENT_FILE_NAME))
                output.output(xmlDocument, it.writer())
                it.closeEntry()
            }
        }
    }

    companion object {
        fun fromPath(containerPath: Path): PhysicalOmniStorage {
            val path = if (containerPath.fileName.toString().endsWith(".ofocus")) {
                containerPath
            } else {
                containerPath.resolve("OmniFocus.ofocus")
            }
            require(Files.exists(path)) { "Specified path does not exist." }
            require(Files.isDirectory(path)) { "Specified path is not a directory and thus cannot be an omnifocus storage path." }
            require(path.fileName.toString().endsWith(".ofocus")) { ".ofocus dir needs to be specified in path." }

            val encryptionFile = Files.list(path).filter { isEncryptionIndicatorFile(it) }.findAny()
            return if (!encryptionFile.isPresent) {
                NormalStorage(path)
            } else {
                EncryptedStorage(path, encryptionFile.get())
            }
        }

        private fun isEncryptionIndicatorFile(file: Path): Boolean {
            return Files.isRegularFile(file) && file.fileName.toString() == FilenameConstants.ENCRYPTED_FILE_NAME
        }
    }
}

interface EncryptedOmniStorage : OmniStorage {
    fun providePassword(password: CharArray)
    fun unencryptedCopy(): OmniStorage
}
