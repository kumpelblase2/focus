package de.eternalwings.focus.commands.task

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.commands.UnlockedStorageBasedCommand
import de.eternalwings.focus.commands.WithDeviceCommand
import de.eternalwings.focus.failWith
import de.eternalwings.focus.view.OmniFocusState
import de.eternalwings.focus.view.OmniTask

abstract class BasicTaskCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false
) : UnlockedStorageBasedCommand(
    help,
    epilog,
    name,
    invokeWithoutSubcommand,
    printHelpOnEmptyArgs,
    helpTags,
    autoCompleteEnvvar,
    allowMultipleSubcommands
), WithDeviceCommand {

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
        val changedTask = applyChanges(task)
        view.updateTask(changedTask, getDevice(storage))
    }

    abstract fun applyChanges(task: OmniTask) : OmniTask
}
