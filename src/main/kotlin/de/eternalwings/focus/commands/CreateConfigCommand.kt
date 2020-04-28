package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.uchuhimo.konf.source.hocon.toHocon
import de.eternalwings.focus.config.ConfigFileProvider
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config
import de.eternalwings.focus.config.save

class CreateConfigCommand :
    CliktCommand(name = "create-config", help = "Creates the configuration for the given store with its password.") {

    val storageLocation by argument("location", help = "The location of the omnifocus store.")
    val password by argument("password", help = "The password to use for the store.").optional()
    val device by argument("device", help = "The ID of the device to use for changes.").optional()

    override fun run() {
        config[Configuration.location] = storageLocation
        config[Configuration.password] = password
        config[Configuration.device] = device
        config.save()
        println("Created configuration at ${ConfigFileProvider.getConfigurationFile()}")
    }
}
