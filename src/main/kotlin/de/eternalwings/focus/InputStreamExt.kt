package de.eternalwings.focus

import java.io.RandomAccessFile

fun RandomAccessFile.readExpecting(size: Int, expected: ByteArray): Boolean {
    val read = ByteArray(size)
    if (this.read(read) < size) {
        return false
    }

    return read.contentEquals(expected)
}

fun RandomAccessFile.readExpecting(expected: ByteArray): Boolean {
    return readExpecting(expected.size, expected)
}

fun RandomAccessFile.readExpecting(size: Int, expected: String): Boolean {
    return readExpecting(size, expected.toByteArray())
}

fun RandomAccessFile.readExpecting(expected: String): Boolean {
    return readExpecting(expected.toByteArray())
}

fun RandomAccessFile.read(size: Int): ByteArray {
    val array = ByteArray(size)
    check(this.read(array) == size)

    return array;
}
