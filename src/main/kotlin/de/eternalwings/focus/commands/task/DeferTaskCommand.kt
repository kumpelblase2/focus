package de.eternalwings.focus.commands.task

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.validate
import de.eternalwings.focus.view.OmniTask
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DeferTaskCommand : BasicTaskCommand(name = "defer", help = "Defer a task to a specific date") {
    val deferTime by argument("defer-time", help = "The date time to defer the task to.").multiple(required = true)
        .validate {
            try {
                DEFER_DATE_FORMAT.parse(it.joinToString(" "))
            } catch (ex: Exception) {
                fail("Not a valid date time format")
            }
        }

    override fun applyChanges(task: OmniTask): OmniTask {
        val dateTime = ZonedDateTime.parse(deferTime.joinToString(" "), DEFER_DATE_FORMAT)
        return task.copy(deferred = dateTime)
    }

    companion object {
        private val DEFER_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault())
    }
}
