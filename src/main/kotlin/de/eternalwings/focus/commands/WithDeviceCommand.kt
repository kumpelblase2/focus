package de.eternalwings.focus.commands

import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.warning

interface WithDeviceCommand : WithDeviceIdCommand, WithDeviceNameCommand {

    fun getDevice(storage: OmniStorage): OmniDevice {
        if (deviceId != null && deviceName != null) {
            warning("Both device name and device id are specified, but only the specified ID will be used. Please specify only one of them avoid causing unexpected results.")
        }

        return if (deviceId != null) {
            getDeviceByName(storage)
        } else {
            getDeviceById(storage)
        }
    }

}
