package de.eternalwings.focus.view

import de.eternalwings.focus.storage.data.WithCreationTimestamp
import java.time.LocalDateTime

data class Creation(
    val creationTime: LocalDateTime,
    val order: Long?
)

fun WithCreationTimestamp.toCreation(): Creation? {
    if(this.added == null) return null
    return Creation(this.added!!, this.order)
}
