package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.presentation.GsonProvider
import de.eternalwings.focus.storage.OmniDevice

data class DeviceHistory(val current: OmniDevice, val previous: List<OmniDevice>)

class DeviceListCommand :
    StorageBasedCommand(name = "list", help = "List the devices that are registered to this store") {

    private val json by option("-j", "--json", help = "If the output should be printed as json").flag()
    private val full by option("--full", help = "Include recorded device history").flag()
    private val gson = GsonProvider.INSTANCE

    override fun run() {
        val storage = loadStorage()
        val byId = storage.devices.groupBy { it.clientId }
        val allDevices = byId.mapNotNull { (_, snapshots) ->
            val current = snapshots.maxByOrNull { it.lastSync }!!
            val history = snapshots.filter { it != current }
            DeviceHistory(current, history)
        }

        if (full) {
            printFull(allDevices)
        } else {
            printNormal(allDevices.map { it.current })
        }
    }

    private fun printNormal(allDevices: List<OmniDevice>) {
        if (json) {
            println(gson.toJson(allDevices))
        } else {
            allDevices.forEach { device ->
                println("Device ${device.clientId}: ${device.name} (${device.model}) Last Sync: ${device.lastSync}")
            }
        }
    }

    private fun printFull(allDevices: List<DeviceHistory>) {
        if (json) {
            println(gson.toJson(allDevices))
        } else {
            allDevices.forEach { (current, history) ->
                println("Device ${current.clientId}: ${current.name} (${current.model}) Last Sync: ${current.lastSync}")
                history.forEach { historyDevice ->
                    println("\tDevice ${historyDevice.clientId}: ${historyDevice.name} (${historyDevice.model}) Last Sync: ${historyDevice.lastSync}")
                }
            }
        }
    }
}
