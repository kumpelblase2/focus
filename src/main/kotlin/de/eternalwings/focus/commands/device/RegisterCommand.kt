package de.eternalwings.focus.commands.device

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.defaultLazy
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.HostnameResolver
import de.eternalwings.focus.commands.StorageBasedCommand
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.IdGenerator
import de.eternalwings.focus.storage.OmniDevice

class RegisterCommand :
    StorageBasedCommand(name = "register", help = "Register this tool as a device in the omnifocus store") {

    val name by argument(
        "name",
        help = "Name of the device to use. Uses the computer hostname per default."
    ).defaultLazy { HostnameResolver.getCurrentHostName() }
    val id by option(
        "--id",
        help = "The ID to use for the device. If not specified a random ID will be used. Has to be length 11, and use only alphanumeric letters with the addition of '-'"
    )
    val model by option("--model", help = "The computer model").default("unspecified")
    val noSave by option("--no-save", help = "Do no use the device for future changes made by omnifocus.").flag()

    override fun run() {
        val storage = loadStorage()
        val idToUse = id ?: IdGenerator.generate(storage.devices.map { it.clientId }.toSet())

        if (idToUse.length != 11) {
            failWith(
                "Specified ID is not of correct length. Needs to be exactly 11 characters long.",
                ErrorCodes.GENERIC_ARGUMENT_ERROR
            )
        }
        val device = OmniDevice.create(name, idToUse, model)
        storage.registerDevice(device)
        println("Registered device '${device.name}' with ID '${device.clientId}'.")
        if(Configuration.instance.device == null || !noSave) {
            Configuration.instance.device = device.clientId
            println("Now using device '${device.name}' as author of changes.")
            Configuration.save()
        }
    }
}
