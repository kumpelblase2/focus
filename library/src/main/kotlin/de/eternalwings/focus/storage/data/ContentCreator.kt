package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.OmniDevice

/**
 * Denotes a user agent that can create be used to create changes in the omnifocus store.
 * Effectively, each device of the user is its own content creator.
 */
data class ContentCreator(
    /**
     * The Apple bundle identifier of the app that is used
     */
    val appId: String,
    /**
     * The version the app is in
     */
    val appVersion: String,
    /**
     * The version of the operating system the app is running on
     */
    val osVersion: String,

    /**
     * The device model identifier
     */
    val machineModel: String
) {

    companion object {
        fun fromDevice(device: OmniDevice) : ContentCreator {
            return ContentCreator(device.bundleId, device.bundleVersion, device.osVersion, device.model)
        }
    }
}
