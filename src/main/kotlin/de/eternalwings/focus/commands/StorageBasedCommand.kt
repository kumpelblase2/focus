package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.PhysicalOmniStorage
import de.eternalwings.focus.warning
import java.nio.file.Path
import java.nio.file.Paths

abstract class StorageBasedCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false
) : CliktCommand(
    help,
    epilog,
    name,
    invokeWithoutSubcommand,
    printHelpOnEmptyArgs,
    helpTags,
    autoCompleteEnvvar,
    allowMultipleSubcommands
) {
    val location by option("--location", "-l", help = "The location for the omnifocus file storage").path(mustExist = true, canBeFile = false)

    fun loadStorage(): PhysicalOmniStorage {
        return OmniStorage.fromPath(getStorageLocation())
    }

    private fun getStorageLocation(): Path {
        return location ?: config[Configuration.location]?.let { Paths.get(it) } ?: failWith(
            "No location provided. Please either set the location in the configuration or provide it after the '--location' flag.",
            ErrorCodes.NO_STORAGE_LOCATION_PROVIDED
        )
    }
}

abstract class UnlockedStorageBasedCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false
) : StorageBasedCommand(
    help,
    epilog,
    name,
    invokeWithoutSubcommand,
    printHelpOnEmptyArgs,
    helpTags,
    autoCompleteEnvvar,
    allowMultipleSubcommands
) {
    protected val password by option("-p", "--password", help = "The password for the omnifocus storage").default("")
    protected val readPassword by option("-P", "--ask-password", help = "Provide password secretly").flag()

    protected fun getUnlockedStorage(): PhysicalOmniStorage {
        val storage = loadStorage()
        if (storage is EncryptedOmniStorage) {
            val password = getPassword()
            storage.providePassword(password)
        }
        return storage
    }

    protected fun getPassword(): CharArray {
        return if (config[Configuration.password] != null) {
            config[Configuration.password]!!.toCharArray()
        } else {
            if (readPassword) {
                readPassword()
            } else {
                val providedPassword = password
                if (providedPassword.isEmpty()) {
                    failWith(
                        "The provided omnifocus storage is encrypted, but no password was given. Please provide the password using -p or -P",
                        ErrorCodes.MISSING_PASSWORD
                    )
                }
                providedPassword.toCharArray()
            }
        }
    }

    protected fun readPassword(): CharArray {
        warning("The provided omnifocus storage is encrypted, please provide the password below.")
        val console = System.console()
        return if (console != null) {
            console.readPassword("Password: ")
        } else {
            warning("Couldn't get access to a console! The input cannot be masked!")
            print("Password: ")
            // We don't need to close the input stream here
            System.`in`.bufferedReader().readLine().toCharArray()
        }
    }
}
