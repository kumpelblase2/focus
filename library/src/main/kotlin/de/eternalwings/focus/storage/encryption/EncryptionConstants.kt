package de.eternalwings.focus.storage.encryption

object EncryptionConstants {
    private const val MAGIC = "OmniFileEncryption"
    val MAGIC_BYTE_DATA = MAGIC.toByteArray() + ByteArray(2) { 0 }
    const val FILE_MAC_PREFIX: Byte = 0x01
    const val FILE_MAC_LENGTH = 32
    const val AES_KEY_SIZE = 16
    const val HMAC_KEY_SIZE = 16
    const val SEG_IV_LENGTH = 12
    const val SEG_MAC_LENGTH = 20
    const val SEG_PAGE_SIZE = 65536
}
