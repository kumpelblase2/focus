package de.eternalwings.focus.storage.encryption

fun ByteArray.asTwoByteInt(): Int {
    require(this.size == 2)
    return (this[1].toInt() shl 8) or this[0].toInt()
}

fun Int.asTwoBytes(): ByteArray {
    val arr = ByteArray(2)
    arr[1] = this.toByte()
    arr[0] = this.shr(8).toByte()
    return arr
}
