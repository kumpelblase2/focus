package de.eternalwings.focus.storage.data

data class Reference(val id: String)

fun String.asReference() = Reference(this)
