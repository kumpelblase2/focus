package de.eternalwings.focus

/**
 * A reference to another element in the store. In many cases this is the reference to the parent element.
 */
data class Reference(
    /**
     * The ID of the referenced element. This may be null to explicitly indicate "no reference", which
     * helps knowing if it should be included in the resulting changeset as there needs to be some way to
     * differentiate "no change" and "change to no parent/reference". `null` alone would not suffice, which
     * is why this ID may be `null` too to indicate this "no reference" situation.
     */
    val id: String? = null
)

internal fun Reference?.mergeInto(other: Reference?): Reference? {
    return when {
        this == null -> other
        this.id == null -> null
        else -> this
    }
}

fun String.asReference() = Reference(this)
