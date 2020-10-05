package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.ChangesetDescription
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

    private var allChangesets: List<Changeset> = changeSets

    override val changesetInformation: List<ChangesetDescription>
        get() = allChangesets.map { it.description }.sortedBy { it.timestamp }

    private val lastChangesetId: String
        get() = changesetInformation.maxByOrNull { it.timestamp }!!.id

    override fun updateDevice(device: OmniDevice, refreshLastSync: Boolean) {
        val updatedDevice = if (refreshLastSync) {
            device.copy(lastSync = OffsetDateTime.now(), tailIds = listOf(lastChangesetId))
        } else {
            device
        }

        devices += updatedDevice

        if (devices.size > 3) {
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
        allChangesets = changeSets + changeset
    }

    override fun getChangesetFor(description: ChangesetDescription): Changeset {
        return allChangesets.find { it.description.id == description.id }!!
    }
}
