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

    fun generate(takenIds: Set<String>, maxTries: Int = 10, length: Int = 11): String {
        for (i in 0..maxTries) {
            val id = generate(length)
            if(!takenIds.contains(id))
                return id
        }

        throw IllegalStateException("Could not generate an ID within the given amount of tries.")
    }
}
