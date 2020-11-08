package de.eternalwings.focus.storage

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FilenameConstants {
    /**
     * Name of the encrypted marker file
     */
    const val ENCRYPTED_FILE_NAME = "encrypted"

    /**
     * Name of the file inside the changeset zip that hold the
     * content of the changeset
     */
    const val CONTENT_FILE_NAME = "contents.xml"

    /**
     * Suffix of files containing client information
     */
    const val CLIENT_FILE_NAME = ".client"

    /**
     * Suffix of files containing capability information
     */
    const val CAPABILITY_FILE_NAME = ".capability"

    /**
     * Formatter for the datetime format used in the changeset files
     */
    val CHANGESET_TIME_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("GMT"))

    /**
     * Timestamp for the "root" changeset
     */
    const val CHANGESET_INIT_TIMESTAMP = "00000000000000"

    /**
     * Formatter for the datetime format used in the client files,
     * which should be the same as the [CHANGESET_TIME_FORMAT]
     */
    val CLIENT_FILE_DATE_FORMAT: DateTimeFormatter = CHANGESET_TIME_FORMAT

    val CHANGESET_FILE_REGEX = "^(\\d{14})=(.{11})\\+(.{11})\\.zip$".toRegex()
}
