package de.eternalwings.focus.commands.task

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.commands.UnlockedStorageBasedCommand
import de.eternalwings.focus.commands.WithDeviceCommand
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask

class CreateTaskCommand :
    UnlockedStorageBasedCommand(name = "create", help = "Adds a new task to a project or the inbox."),
    WithDeviceCommand {

    val text by argument("task").multiple(true)
    override val deviceName by option(
        "--device",
        "-d",
        help = "The name of the device to use. Uses the configured device by default."
    )
    override val deviceId by option(
        "--device-id",
        "-i",
        help = "The id of the device to use. Uses the configured device by default."
    )

    override fun run() {
        val storage = getUnlockedStorage()
        val view = OmniFocusState(storage, true)
        val task = OmniTask.create(view.generateUnusedId(), text.joinToString(" ")) {

        }
        view.createTask(task, getDevice(storage))
        println(task.id)
    }
}
