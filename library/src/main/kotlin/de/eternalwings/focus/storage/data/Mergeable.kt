package de.eternalwings.focus.storage.data

interface Mergeable<T> {
    fun mergeFrom(other: T): T
}
