package de.eternalwings.focus.view

import de.eternalwings.focus.storage.data.WithCreationTimestamp
import java.time.ZonedDateTime

data class Creation(
    val creationTime: ZonedDateTime,
    val order: Long?
)

fun WithCreationTimestamp.toCreation(): Creation? {
    if (this.added == null) return null
    return Creation(this.added!!, this.order)
}
