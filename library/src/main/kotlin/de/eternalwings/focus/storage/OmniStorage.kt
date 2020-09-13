package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.*
import de.eternalwings.focus.storage.data.xml.OmniContainerXmlConverter
import de.eternalwings.focus.storage.plist.Plist
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * An [OmniStorage] that has a "physical" representation on this computer.
 */
interface PhysicalOmniStorage : OmniStorage {
    /**
     * The filesystem location of this storage
     */
    val location: Path

    /**
     * The files that contain the changesets of this storage
     */
    val changeSetFiles: List<ChangesetFile>

    /**
     * Returns the data inside the given changeset. Normal changesets are zip files containing an xml file. This
     * returns the contents of that xml file.
     */
    fun getContentOfFile(file: ChangesetFile): ByteArray

    /**
     * Save the given changeset in the physical storage location.
     */
    fun save(changeset: Changeset)
}

/**
 * An object representing the underlying OmniFocus database.
 */
interface OmniStorage {
    /**
     * The devices registered to this storage. This includes past versions of the same client, thus
     * this list may contain the same client multiple times.
     */
    val devices: Collection<OmniDevice>

    /**
     * Same as [devices] but only the most recent versions of each device.
     */
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

    /**
     * The capabilities (i.e. features) that are enabled on this storage.
     */
    val capabilities: Collection<OmniCapability>

    /**
     * The changesets (or "transactions") that are present in this storage.
     */
    val changeSets: List<Changeset>

    /**
     * Registers a new device in this storage which can then be used to track
     * synchronization progress as well as author changesets.
     *
     * @throws IllegalArgumentException if a device with the same ID already exists in the store.
     *  To update a device use [updateDevice] instead
     */
    fun registerDevice(device: OmniDevice) {
        if (devices.any { it.clientId == device.clientId }) {
            throw IllegalArgumentException("A device with the given ID already exists. Did you mean to update it?")
        }

        val tailIds = changeSets.asSequence().sortedByDescending { it.timestamp }.first()
        this.updateDevice(device.copy(tailIds = listOf(tailIds.id)))
    }

    /**
     * Removes a device from the store, including all it's versions.
     */
    fun removeDevice(clientId: String)

    /**
     * Saves an update version of the given device pointing to the most recent changesets.
     * This implicitly sets the [OmniDevice.lastSync] & [OmniDevice.tailIds] to the most
     * recent values (now & last changeset id respectively). If you want to manually control
     * these values, use the overloaded variant of this method.
     */
    fun updateDevice(device: OmniDevice) {
        this.updateDevice(device, true)
    }

    /**
     * Saves the given device to the store. If more than three device versions of the same device
     * are present, a cleanup will be performed to keep only the freshest three versions of this
     * given device.
     *
     * @param device The device to be saved
     * @param refreshLastSync If the [lastSync][OmniDevice.lastSync] & [tailIds][OmniDevice.tailIds] properties should be
     *   updated as well before saving.
     */
    fun updateDevice(device: OmniDevice, refreshLastSync: Boolean)

    /**
     * Creates a changeset with the given content. This does _not_ persist the changeset in the
     * store yet; use [appendChangeset] for that.
     */
    fun prepareChangeset(creator: OmniDevice, vararg elements: ChangesetElement): Changeset {
        val id = IdGenerator.generate(changeSets.map { it.id }.toSet())
        return Changeset(
            LocalDateTime.now(),
            id,
            changeSets.last().id,
            OmniContainer(ContentCreator.fromDevice(creator), listOf(*elements))
        )
    }

    /**
     * Queues a changeset to be stored, but will not be persisted to disk yet. If you want to
     * directly store this changeset, use the overloaded variant of this method to control this
     * persisting.
     */
    fun appendChangeset(changeset: Changeset) {
        appendChangeset(changeset, false)
    }

    fun appendChangeset(changeset: Changeset, persist: Boolean)

    /**
     * Copies the data contained in this storage to another location. This is not a file-by-file copy,
     * but instead reads the content of the old storage as well as any programmatic changes made and write those to the new location.
     *
     * @param location The directory the storage will be saved into. Usually this directory is called "OmniFocus.ofocus".
     */
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
            val xmlDocument = OmniContainerXmlConverter.write(changeset.container)
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

    fun findDeviceById(id: String): OmniDevice? {
        return uniqueDevices.find { it.clientId == id }
    }

    fun findDeviceByName(name: String): OmniDevice? {
        return uniqueDevices.find { it.name == name }
    }

    companion object {
        /**
         * Creates an in memory representation of the OmniFocus database found at the given location.
         *
         * @return An [OmniStorage] baked by physical files that may or may not be [EncryptedOmniStorage].
         * @throws IllegalArgumentException When the path does not exist or is not a directory ending with ".ofocus"
         */
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
    /**
     * Provide the password for this encrypted storage to be used to decrypt the files within.
     */
    fun providePassword(password: CharArray)

    /**
     * Creates an in memory copy of this storage but unencrypted. This can can be saved via [saveTo] to create an
     * unencrypted copy in the filesystem.
     */
    fun unencryptedCopy(): OmniStorage
}
