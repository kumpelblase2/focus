package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.failWith
import de.eternalwings.focus.view.OmniFocusState
import java.time.ZonedDateTime

class DoTaskCommand : UnlockedStorageBasedCommand(name = "do", help = "Marks a task as done"), WithDeviceCommand {

    val id by argument(name = "task-id", help = "The ID of the task that is done")
    override val deviceName by option(
        "--device", "-d",
        help = "The name of the device to use. Uses the configured device by default."
    )
    override val deviceId by option(
        "--device-id", "-i",
        help = "The id of the device to use. Uses the configured device by default."
    )

    override fun run() {
        val storage = getUnlockedStorage()
        val view = OmniFocusState(storage, true)
        val task =
            view.getTaskById(id) ?: failWith("Could not find task with id $id", ErrorCodes.GENERIC_ARGUMENT_ERROR)

        val doneTask = task.copy(completed = ZonedDateTime.now())
        view.updateTask(doneTask, getDevice(storage))
    }
}
