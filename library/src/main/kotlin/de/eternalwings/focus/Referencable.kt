package de.eternalwings.focus

/**
 * An element that may be referenced by other elements.
 */
interface Referencable {
    /**
     * The ID of this element. This ID should also be used to make references to this element.
     */
    val id: String
}
