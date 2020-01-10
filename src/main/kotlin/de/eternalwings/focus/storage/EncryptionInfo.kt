package de.eternalwings.focus.storage

import de.eternalwings.plist.DictionaryObject

data class EncryptionInfo(
    val algorithm: String,
    val key: ByteArray,
    val method: String,
    val rounds: Int,
    val salt: ByteArray
) {
    companion object {
        fun fromPlist(plist: DictionaryObject): EncryptionInfo {
            return with(plist.content) {
                EncryptionInfo(
                    this["algorithm"]!!.get(),
                    this["key"]!!.get(),
                    this["method"]!!.get(),
                    this["rounds"]!!.get(),
                    this["salt"]!!.get()
                )
            }
        }
    }
}
