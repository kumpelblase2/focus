package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class StorageCommand : NoOpCliktCommand(name = "storage", help = "Storage related commands") {
    init {
        subcommands(DecryptCommand(), ListFilesInStorageCommand(), ShowFileCommand())
    }
}
