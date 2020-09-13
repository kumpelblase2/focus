package de.eternalwings.focus.storage.data

/**
 * Defines the operation a changeset element applies on the omnifocus store.
 */
enum class Operation {
    /**
     * Applies an update to an element and it's properties
     */
    UPDATE,

    /**
     * Creates a new element
     */
    CREATE,

    /**
     * Deletes an element
     */
    DELETE,

    /**
     * Serves as a reference to make sure the element in the store of the application
     * matches the state of this element.
     */
    REFERENCE
}

fun String.toOperation(): Operation {
    return Operation.valueOf(this.toUpperCase())
}
