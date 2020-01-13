package de.eternalwings.focus.storage.data

interface WithOperation {
    val operation: Operation
}

enum class Operation {
    UPDATE,
    CREATE,
    DELETE,
    REFERENCE
}

fun String.toOperation() : Operation {
    return Operation.valueOf(this.toUpperCase())
}
