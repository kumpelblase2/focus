package de.eternalwings.focus.storage.watcher

import de.eternalwings.focus.storage.PhysicalOmniStorage
import de.eternalwings.focus.storage.data.ChangesetFilenameParser
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

class WatchserviceChangesetWatcher(pathToWatch: Path) : BaseChangesetWatcher(pathToWatch) {
    private var listenThread: Thread? = null

    constructor(storage: PhysicalOmniStorage) : this(storage.location)

    init {
    }

    override fun watch() {
        listenThread = Thread(this::listen, "ChangesetWatchThread")
        listenThread!!.start()
    }

    override fun unwatch() {
        listenThread?.interrupt()
    }

    private fun listen() {
        /*

        This approach generally should work: a file get created on the local system with the new changeset
        and we get notified on this change and pick it up. However, since we're depending on some external
        service to sync the files, e.g. dropbox, nextcloud, etc., we don't know how they implement it.

        In my case, using nextcloud, this straight forward mechanism doesn't work as it will create a temporary,
        invisible file and once the sync is complete it will rename the file to the correct filename. This does
        _not_ trigger an ENTRY_CREATE node - unfortunately - and we thus don't catch it. I'm reluctant to
        specifically handle such a case as that _should_ be an implementation detail. I could see providing a
        param specifying how the watching should be done and/or some platform specific implementations that
        would catch this scenario, as linux would have a specific rename event available.

        For now, though, I'm inclined to just leave it as-is until I deem this more important.

         */

        val watchService = FileSystems.getDefault().newWatchService()
        pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)

        try {
            while (true) {
                val watchKey = watchService.take()
                watchKey.pollEvents().asSequence().forEach { event ->
                    val file = event.context() as Path
                    val changeset = ChangesetFilenameParser.getInformationOf(file)
                    if (changeset != null) {
                        when (event.kind()) {
                            StandardWatchEventKinds.ENTRY_CREATE -> {
                                val changesetAddEvent = ChangesetChangeEvent.ChangesetAddEvent(changeset)
                                addListeners.forEach { it.invoke(changesetAddEvent) }
                                updateListeners.forEach { it.run() }
                            }
                            StandardWatchEventKinds.ENTRY_DELETE -> {
                                val changesetRemoveEvent = ChangesetChangeEvent.ChangesetRemoveEvent(changeset)
                                removeListeners.forEach { it.invoke(changesetRemoveEvent) }
                                updateListeners.forEach { it.run() }
                            }
                        }
                    }
                }
                watchKey.reset()
            }
        } catch (ex: InterruptedException) {
            watchService.close()
        }
    }
}
