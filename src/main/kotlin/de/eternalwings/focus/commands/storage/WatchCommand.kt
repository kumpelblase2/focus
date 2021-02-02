package de.eternalwings.focus.commands.storage

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.long
import de.eternalwings.focus.commands.StorageBasedCommand
import de.eternalwings.focus.storage.watcher.PollingChangesetWatcher
import de.eternalwings.focus.storage.watcher.WatchserviceChangesetWatcher

class WatchCommand : StorageBasedCommand(
    name = "watch",
    help = "Watch the storage for new changesets.",
) {
    val type by argument(name = "WATCH_TYPE").choice("poll", "service").optional()
    val pollInterval by option("--interval", help = "Polling interval when using poll type").long().default(30000)

    override fun run() {
        val storage = loadStorage()
        val watcher = when (type) {
            "poll" -> PollingChangesetWatcher(storage, pollInterval)
            else -> WatchserviceChangesetWatcher(storage)
        }
        watcher.onAdd {
            echo("New changeset with ID ${it.changesetDescription.id} arrived with timestamp ${it.changesetDescription.timestamp}. (Previous ID: ${it.changesetDescription.previousId})")
        }

        watcher.onRemove {
            echo("Changeset with ID ${it.changesetDescription.id} got removed. (Previous ID: ${it.changesetDescription.previousId})")
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            watcher.unwatch()
        })
        watcher.watch()
    }

}
