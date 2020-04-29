package de.eternalwings.focus.storage

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FilenameConstants {
    const val ENCRYPTED_FILE_NAME = "encrypted"
    const val CONTENT_FILE_NAME = "contents.xml"
    const val CLIENT_FILE_NAME = ".client"
    const val CAPABILITY_FILE_NAME = ".capability"
    val CHANGESET_TIME_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("GMT"))
    const val CHANGESET_INIT_TIMESTAMP = "00000000000000"
    val CLIENT_FILE_DATE_FORMAT: DateTimeFormatter = CHANGESET_TIME_FORMAT
}
