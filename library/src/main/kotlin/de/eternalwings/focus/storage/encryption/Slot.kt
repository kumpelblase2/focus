package de.eternalwings.focus.storage.encryption

internal data class Slot(val type: SlotType, val index: Int, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Slot) return false

        if (type != other.type) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + index
        return result
    }
}

enum class SlotType {
    NONE,
    ACTIVE_AES_WRAP,
    RETIRED_AES_WRAP,
    ACTIVE_AES_CTR_HMAC,
    RETIRED_AES_CTR_HMAC,
    PLAINTEXT_MASK,
    RETIRED_PLAINTEXT_MASK;

    companion object {
        fun fromIndex(ordinal: Int): SlotType {
            return values().find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}
