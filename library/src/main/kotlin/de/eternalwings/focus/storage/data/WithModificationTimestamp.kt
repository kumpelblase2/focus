package de.eternalwings.focus.storage.data

import java.time.ZonedDateTime

interface WithModificationTimestamp {
    val modified: ZonedDateTime?
}
