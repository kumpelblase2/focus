package de.eternalwings.focus.storage.watcher

import de.eternalwings.focus.storage.PhysicalOmniStorage
import de.eternalwings.focus.storage.data.ChangesetDescription
import de.eternalwings.focus.storage.data.ChangesetFilenameParser
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class PollingChangesetWatcher(pathToWatch: Path, private val pollInterval: Long = 30000) :
    BaseChangesetWatcher(pathToWatch) {
    private var currentChangesets: List<ChangesetDescription> = emptyList()
    private var thread: Thread? = null

    constructor(storage: PhysicalOmniStorage, pollInterval: Long = 30000) : this(storage.location, pollInterval)

    init {
        currentChangesets = getCurrentDescriptions()
    }

    override fun watch() {
        thread = Thread(this::loop, "PollingChangesetWatcher")
        thread!!.start()
    }

    override fun unwatch() {
        thread?.interrupt()
    }

    private fun loop() {
        try {
            while (true) {
                Thread.sleep(pollInterval)
                this.poll()
            }
        } catch (ex: InterruptedException) {
            // Do nothing since we want to exit the loop
        }
    }

    private fun poll() {
        val updatedDescription = getCurrentDescriptions()
        updatedDescription.forEach { changeset ->
            if (!currentChangesets.any { oldChangeset -> oldChangeset.id == changeset.id }) {
                val event = ChangesetChangeEvent.ChangesetAddEvent(changeset)
                addListeners.forEach { it.invoke(event) }
            }
        }

        currentChangesets.forEach { oldChangeset ->
            if (!updatedDescription.any { newChangeset -> newChangeset.id == oldChangeset.id }) {
                val event = ChangesetChangeEvent.ChangesetRemoveEvent(oldChangeset)
                removeListeners.forEach { it.invoke(event) }
            }
        }

        updateListeners.forEach { it.run() }

        currentChangesets = updatedDescription
    }

    private fun getCurrentDescriptions(): List<ChangesetDescription> {
        return Files.list(pathToWatch).map { path ->
            ChangesetFilenameParser.getInformationOf(path)
        }.toList().filterNotNull()
    }
}
