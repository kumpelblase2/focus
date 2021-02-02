package de.eternalwings.focus.commands.storage

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.commands.UnlockedStorageBasedCommand
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.EncryptedOmniStorage
import java.nio.file.Files
import java.nio.file.Path

class DecryptCommand : UnlockedStorageBasedCommand(
    name = "decrypt",
    help = "Decrypts the omnifocus store and saves it to a new location."
) {
    private val targetLocation by argument(
        "target-location",
        help = "The location where the unecrypted copy should be stored."
    ).path()

    override fun run() {
        val storage = getUnlockedStorage()
        if (storage is EncryptedOmniStorage) {
            val container = prepareContainer()
            val unencrypted = storage.unencryptedCopy()
            unencrypted.saveTo(container)
        } else {
            failWith("Storage isn't encrypted so there's nothing to decrypt.", ErrorCodes.GENERIC_ARGUMENT_ERROR)
        }
    }

    private fun prepareContainer(): Path {
        return if (Files.exists(targetLocation)) {
            // Make sure we don't accidentally overwrite an existing store ...
            if (Files.list(targetLocation).count() > 0) {
                failWith(
                    "The specified location already contains files. Either specify a different location or clean it.",
                    ErrorCodes.GENERIC_ARGUMENT_ERROR
                )
            }
            targetLocation
        } else {
            Files.createDirectories(targetLocation)
        }
    }
}
