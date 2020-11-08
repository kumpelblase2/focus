package de.eternalwings.focus.storage.watcher

import java.nio.file.Path

abstract class BaseChangesetWatcher(override val pathToWatch: Path) : ChangesetWatcher {
    protected var addListeners: List<ChangesetAddEventListener> = emptyList()
    protected var removeListeners: List<ChangesetRemoveEventListener> = emptyList()
    protected var updateListeners: List<Runnable> = emptyList()


    override fun onAdd(listener: ChangesetAddEventListener) {
        addListeners = addListeners + listener
    }

    override fun onRemove(listener: ChangesetRemoveEventListener) {
        removeListeners = removeListeners + listener
    }

    override fun onCompleteUpdate(listener: Runnable) {
        updateListeners = updateListeners + listener
    }
}
