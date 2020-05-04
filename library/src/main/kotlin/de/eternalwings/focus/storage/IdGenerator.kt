package de.eternalwings.focus.storage

/**
 * Generator for generating valid IDs that can be understood by OmniFocus.
 */
object IdGenerator {
    private val alphabet = ('0'..'9') + ('A'..'Z') + ('a'..'z')

    /**
     * Generates a random ID with the given length (default: 11) with the accepted characters by OmniFocus.
     */
    fun generate(length: Int = 11): String {
        return (0 until length).map { alphabet.random() }.joinToString("")
    }
}
