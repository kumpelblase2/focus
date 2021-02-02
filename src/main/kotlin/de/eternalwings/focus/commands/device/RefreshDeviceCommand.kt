package de.eternalwings.focus.commands.device

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import de.eternalwings.focus.commands.StorageBasedCommand
import de.eternalwings.focus.commands.WithDeviceIdCommand

class RefreshDeviceCommand : StorageBasedCommand(name = "refresh", help = "Updates a device to the newest changeset"),
    WithDeviceIdCommand {

    override val deviceId by argument("device", help = "The id of the device to update.").optional()

    override fun run() {
        val storage = loadStorage()
        val newestDevice = getDeviceById(storage)
        storage.updateDevice(newestDevice)
    }

}
