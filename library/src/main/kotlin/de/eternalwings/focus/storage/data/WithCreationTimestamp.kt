package de.eternalwings.focus.storage.data

import java.time.LocalDateTime

interface WithCreationTimestamp {
    val added: LocalDateTime?
    val order: Long?
}
