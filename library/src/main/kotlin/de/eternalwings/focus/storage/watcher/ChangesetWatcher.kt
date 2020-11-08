package de.eternalwings.focus.storage.watcher

import de.eternalwings.focus.storage.data.ChangesetDescription
import java.nio.file.Path

sealed class ChangesetChangeEvent {
    data class ChangesetAddEvent(val changesetDescription: ChangesetDescription) : ChangesetChangeEvent()
    data class ChangesetRemoveEvent(val changesetDescription: ChangesetDescription) : ChangesetChangeEvent()
}

typealias ChangesetAddEventListener = (ChangesetChangeEvent.ChangesetAddEvent) -> Unit
typealias ChangesetRemoveEventListener = (ChangesetChangeEvent.ChangesetRemoveEvent) -> Unit

interface ChangesetWatcher {
    val pathToWatch: Path

    fun onAdd(listener: ChangesetAddEventListener)
    fun onRemove(listener: ChangesetRemoveEventListener)
    fun onCompleteUpdate(listener: Runnable)
    fun watch()
    fun unwatch()
}
