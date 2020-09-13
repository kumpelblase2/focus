package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import java.time.OffsetDateTime

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

    private val lastChangesetId: String
        get() = changeSets.maxByOrNull { it.timestamp }!!.id

    override fun updateDevice(device: OmniDevice, refreshLastSync: Boolean) {
        val updatedDevice = if(refreshLastSync) {
            device.copy(lastSync = OffsetDateTime.now(), tailIds = listOf(lastChangesetId))
        } else {
            device
        }

        devices += updatedDevice

        if(devices.size > 3) {
            val changesetsForDevice = devices.asSequence().filter { it.clientId == updatedDevice.clientId }
            val sorted = changesetsForDevice.sortedByDescending { it.lastSync }
            val oldestKept = sorted.take(3).last()
            devices = devices.filter { it.clientId != updatedDevice.clientId || it.lastSync > oldestKept.lastSync }
        }
    }

    override fun removeDevice(clientId: String) {
        devices = devices.filter { it.clientId != clientId }
    }

    override fun appendChangeset(changeset: Changeset, persist: Boolean) {
        changeSets = changeSets + changeset
    }
}
