package de.eternalwings.focus.view

interface Mergeable<T,R> {
    fun mergeFrom(other: R): T
}
