package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import de.eternalwings.focus.config.ConfigFileProvider
import de.eternalwings.focus.config.Configuration

class CreateConfigCommand :
    CliktCommand(name = "create-config", help = "Creates the configuration for the given store with its password.") {

    val storageLocation by argument("location", help = "The location of the omnifocus store.")
    val password by argument("password", help = "The password to use for the store.").optional()
    val device by argument("device", help = "The ID of the device to use for changes.").optional()

    override fun run() {
        Configuration.instance.location = storageLocation
        Configuration.instance.password = password
        Configuration.instance.device = device
        Configuration.save()
        println("Created configuration at ${ConfigFileProvider.getConfigurationFile()}")
    }
}
