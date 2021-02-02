package de.eternalwings.focus.commands.task

import de.eternalwings.focus.view.OmniTask
import java.time.ZonedDateTime

class DoTaskCommand : BasicTaskCommand(name = "do", help = "Marks a task as done") {

    override fun applyChanges(task: OmniTask): OmniTask {
        return task.copy(completed = ZonedDateTime.now())
    }
}
