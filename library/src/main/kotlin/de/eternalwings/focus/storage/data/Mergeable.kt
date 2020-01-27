package de.eternalwings.focus.storage.data

interface Mergeable<T,R> {
    fun mergeFrom(other: R): T
}
