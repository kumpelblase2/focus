package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset

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

    override fun removeDevice(clientId: String) {
        devices = devices.filter { it.clientId != clientId }
    }
}
