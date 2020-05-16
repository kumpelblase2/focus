package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.view.OmniFocusState
import java.time.ZonedDateTime

class DoTaskCommand : UnlockedStorageBasedCommand(name = "do", help = "Marks a task as done") {

    val id by argument(name = "task-id", help = "The ID of the task that is done")
    val device by option("--device", "-d", help = "The device to use. Uses the configured device by default.")

    override fun run() {
        val storage = getUnlockedStorage()
        val view = OmniFocusState(storage, true)
        val task =
            view.getTaskById(id) ?: failWith("Could not find task with id $id", ErrorCodes.GENERIC_ARGUMENT_ERROR)

        val doneTask = task.copy(completed = ZonedDateTime.now())
        view.updateTask(doneTask, getDevice(storage))
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
