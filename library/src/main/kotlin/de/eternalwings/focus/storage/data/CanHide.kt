package de.eternalwings.focus.storage.data

/**
 * Defines an element that may be hidden by being marked as 'blocked'.
 * For example, a context which may be set to mark all child elements as 'blocked'.
 */
interface CanHide {
    val hidden: Boolean?
}
