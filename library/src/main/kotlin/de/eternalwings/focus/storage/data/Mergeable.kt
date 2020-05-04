package de.eternalwings.focus.storage.data

/**
 * Describes elements that can be merged with themselves
 */
interface Mergeable<T> {
    /**
     * Creates a new element that is the result of merging this element with the given element.
     */
    fun mergeFrom(other: T): T
}
