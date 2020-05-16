package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask

class CreateTaskCommand :
    UnlockedStorageBasedCommand(name = "create", help = "Adds a new task to a project or the inbox.") {

    val text by argument("task").multiple(true)
    val device by option("--device", "-d", help = "The device to use. Uses the configured device by default.")

    override fun run() {
        val storage = getUnlockedStorage()
        val view = OmniFocusState(storage, true)
        val task = OmniTask.create(view.generateUnusedId(), text.joinToString(" ")) {

        }
        view.createTask(task, getDevice(storage))
        println(task.id)
    }

    private fun getDevice(storage: OmniStorage): OmniDevice {
        val deviceId = device ?: Configuration.instance.device ?: failWith(
            "No device ID was specified.",
            ErrorCodes.GENERIC_ARGUMENT_ERROR
        )

        return storage.uniqueDevices.find { it.clientId == deviceId }
            ?: failWith("A device with the id $deviceId was not found.", ErrorCodes.GENERIC_ARGUMENT_ERROR)
    }
}
