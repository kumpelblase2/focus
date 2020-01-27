package de.eternalwings.focus.view

enum class RepetitionMethod {
    FIXED,
    START_AFTER_COMPLETION,
    DUE_AFTER_COMPLETION;

    companion object {
        fun fromString(value: String): RepetitionMethod {
            if(value.isEmpty()) return FIXED
            return valueOf(value.toUpperCase().replace("-", "_"))
        }
    }
}
