package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.plist.Plist
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class InMemoryStorage(
    devices: Collection<OmniDevice> = emptyList(),
    capabilities: Collection<OmniCapability> = emptyList(),
    changeSets: List<Changeset> = emptyList()
) : OmniStorage {
    override var devices: Collection<OmniDevice> = devices
        private set
    override var capabilities: Collection<OmniCapability> = capabilities
        private set
    override var changeSets: List<Changeset> = changeSets
        private set

    override fun registerDevice(device: OmniDevice) {
        devices += device
    }
}
