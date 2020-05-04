package de.eternalwings.focus.storage.encryption

import de.eternalwings.focus.read
import de.eternalwings.focus.storage.encryption.EncryptionConstants.FILE_MAC_PREFIX
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_IV_LENGTH
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_MAC_LENGTH
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_PAGE_SIZE
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal data class Segment(val index: Int, val start: Int, val length: Int)

class FileDecryptor(aesKey: ByteArray, private val hmacKey: ByteArray) {
    private val aesKey: SecretKey = SecretKeySpec(aesKey, "AES")

    private fun getSegments(start: Int, end: Int): List<Segment> {
        val encryptedHeaderSize = SEG_IV_LENGTH + SEG_MAC_LENGTH
        var index = 0
        var position = start
        val segments = emptyList<Segment>().toMutableList()
        while (true) {
            if (position + encryptedHeaderSize + SEG_PAGE_SIZE > end) {
                segments += Segment(index, position, end - (position + encryptedHeaderSize))
                break
            } else {
                segments += Segment(index, position, SEG_PAGE_SIZE)
                position += encryptedHeaderSize + SEG_PAGE_SIZE
                index += 1
            }
        }

        return segments
    }

    fun checkHMAC(fileInput: RandomAccessFile, start: Int, end: Int, fileMac: ByteArray) {
        val hmac = createHmac()
        val segmentMac = createHmac()
        hmac.update(FILE_MAC_PREFIX)
        for (segment in getSegments(start, end)) {
            fileInput.seek(segment.start.toLong())
            val segmentIV = fileInput.read(SEG_IV_LENGTH)
            val segmentMAC = fileInput.read(SEG_MAC_LENGTH)


            segmentMac.update(segmentIV)
            segmentMac.update(segment.index.toByteArray())
            segmentMac.update(fileInput.read(segment.length))

            val computed = segmentMac.doFinal()
            check(computed.copyOfRange(0, SEG_MAC_LENGTH).contentEquals(segmentMAC))

            hmac.update(segmentMAC)
        }

        check(hmac.doFinal()!!.contentEquals(fileMac))
    }

    fun decrypt(fileInput: RandomAccessFile, start: Int, end: Int): ByteArray {
        val buffer = ByteBuffer.allocate(end - start)
        val aes = Cipher.getInstance("AES/CTR/NOPADDING")
        for (segment in getSegments(start, end)) {
            fileInput.seek(segment.start.toLong())
            val segmentIV = fileInput.read(SEG_IV_LENGTH)
            val initialIV = IvParameterSpec(segmentIV + ByteArray(4) { 0 })
            aes.init(Cipher.DECRYPT_MODE, aesKey, initialIV)
            fileInput.seek(segment.start.toLong() + SEG_IV_LENGTH + SEG_MAC_LENGTH)
            buffer.put(aes.update(fileInput.read(segment.length)))
            val final = aes.doFinal()
            if (final.isNotEmpty())
                buffer.put(final)
        }

        return buffer.array()
    }

    private fun createHmac(): Mac {
        return Mac.getInstance("HmacSHA256").also {
            it.init(SecretKeySpec(hmacKey, "HmacSHA256"))
        }
    }

}

fun Int.toByteArray(): ByteArray {
    val bufferSize = Int.SIZE_BYTES
    val buffer = ByteBuffer.allocate(bufferSize)
    buffer.order(ByteOrder.BIG_ENDIAN)
    buffer.putInt(this)
    return buffer.array()
}
