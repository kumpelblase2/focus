package de.eternalwings.focus

data class Reference(val id: String)

fun String.asReference() = Reference(this)
