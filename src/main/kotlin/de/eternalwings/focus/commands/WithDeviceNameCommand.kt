package de.eternalwings.focus.commands

import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage

interface WithDeviceNameCommand {
    val deviceName: String?

    fun getDeviceByName(storage: OmniStorage): OmniDevice {
        if (deviceName != null) {
            return storage.uniqueDevices.find { it.name == deviceName } ?: failWith(
                "A device with the name $deviceName was not found.",
                ErrorCodes.GENERIC_ARGUMENT_ERROR
            )
        }

        return storage.uniqueDevices.find { it.clientId == Configuration.instance.device }
            ?: failWith(
                "Could not use registered device because a device with id ${Configuration.instance.device} does not exist.",
                ErrorCodes.GENERIC_ARGUMENT_ERROR
            )
    }

}
