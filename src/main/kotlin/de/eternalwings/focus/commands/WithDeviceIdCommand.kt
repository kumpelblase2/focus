package de.eternalwings.focus.commands

import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage

interface WithDeviceIdCommand {

    val deviceId: String?

    fun getDeviceById(storage: OmniStorage): OmniDevice {
        val id = deviceId ?: Configuration.instance.device ?: failWith(
            "No device was specified. Either set a device in the configuration or pass it the command.",
            ErrorCodes.GENERIC_ARGUMENT_ERROR
        )

        return storage.uniqueDevices.find { it.clientId == id } ?: failWith(
            "Could not use registered device because a device with id $id does not exist.",
            ErrorCodes.GENERIC_ARGUMENT_ERROR
        )
    }

}
