package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import de.eternalwings.focus.commands.storage.DecryptCommand
import de.eternalwings.focus.commands.storage.ListFilesInStorageCommand
import de.eternalwings.focus.commands.storage.ShowFileCommand
import de.eternalwings.focus.commands.storage.WatchCommand

class StorageCommand : NoOpCliktCommand(name = "storage", help = "Storage related commands") {
    init {
        subcommands(DecryptCommand(), ListFilesInStorageCommand(), ShowFileCommand(), WatchCommand())
    }
}
