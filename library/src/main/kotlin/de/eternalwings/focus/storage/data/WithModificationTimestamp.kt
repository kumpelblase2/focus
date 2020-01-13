package de.eternalwings.focus.storage.data

import java.time.LocalDateTime

interface WithModificationTimestamp {
    val modified: LocalDateTime?
}
