package de.eternalwings.focus.commands.task

import de.eternalwings.focus.view.OmniTask
import java.time.ZonedDateTime

class CancelTaskCommand : BasicTaskCommand(name = "cancel", help = "Cancels the given task") {
    override fun applyChanges(task: OmniTask): OmniTask {
        return task.copy(dropped = ZonedDateTime.now())
    }
}
