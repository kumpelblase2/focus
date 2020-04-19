package de.eternalwings.focus.storage.data

import java.time.ZonedDateTime

interface WithCreationTimestamp {
    val added: ZonedDateTime?
    val order: Long?
}
