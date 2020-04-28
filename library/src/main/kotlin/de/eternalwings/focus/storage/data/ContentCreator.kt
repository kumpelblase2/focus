package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.OmniDevice

data class ContentCreator(
    val appId: String,
    val appVersion: String,
    val osVersion: String,
    val machineModel: String
) {

    companion object {
        fun fromDevice(device: OmniDevice) : ContentCreator {
            return ContentCreator(device.bundleId, device.bundleVersion, device.osVersion, device.model)
        }
    }
}
