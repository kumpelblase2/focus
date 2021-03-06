package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.EncryptedOmniStorage
import de.eternalwings.focus.storage.OmniStorage
import de.eternalwings.focus.storage.PhysicalOmniStorage
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
    val location by option(
        "--location",
        "-l",
        help = "The location for the omnifocus file storage"
    ).path(mustExist = true, canBeFile = false)

    fun loadStorage(): PhysicalOmniStorage {
        return OmniStorage.fromPath(getStorageLocation())
    }

    private fun getStorageLocation(): Path {
        return location ?: Configuration.instance.location?.let { Paths.get(it) } ?: failWith(
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
            val password = getPassword(storage.location)
            storage.providePassword(password)
        }
        return storage
    }

    protected fun getPassword(storagePath: Path): CharArray {
        val matchesLocation = Configuration.instance.location?.let { storagePath == Paths.get(it) } ?: false
        return if (Configuration.instance.password != null && matchesLocation) {
            Configuration.instance.password!!.toCharArray()
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
        return TermUi.prompt("Password: ", hideInput = true)?.toCharArray() ?: charArrayOf()
    }
}
