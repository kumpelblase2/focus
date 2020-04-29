package de.eternalwings.focus.commands

class DeviceListCommand :
    StorageBasedCommand(name = "list", help = "List the devices that are registered to this store") {
    override fun run() {
        val storage = loadStorage()
        val byId = storage.devices.groupBy { it.clientId }
        byId.forEach { (device, snapshots) ->
            val mostRecentDevice = snapshots.maxBy { it.lastSync }!!
            println("Device $device: ${mostRecentDevice.name} (${mostRecentDevice.model}) Last Sync: ${mostRecentDevice.lastSync}")
        }
    }
}
