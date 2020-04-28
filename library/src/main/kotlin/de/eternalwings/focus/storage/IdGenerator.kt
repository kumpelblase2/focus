package de.eternalwings.focus.storage

object IdGenerator {
    private val alphabet = ('0'..'9') + ('A'..'Z') + ('a'..'z')

    fun generate(length: Int = 11): String {
        return (0 until length).map { alphabet.random() }.joinToString("")
    }
}
