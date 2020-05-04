package de.eternalwings.focus.storage.encryption

import de.eternalwings.focus.storage.encryption.EncryptionConstants.MAGIC_BYTE_DATA
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_IV_LENGTH
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_MAC_LENGTH
import de.eternalwings.focus.storage.encryption.EncryptionConstants.SEG_PAGE_SIZE
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class FileEncryptor(
    private val keyId: Int,
    private val aesKey: SecretKey,
    private val hmacKey: SecretKey,
    private val wrappedKey: ByteArray
) {

    fun createPreamble(): ByteArray {
        val keyIndex = keyId.asTwoBytes()
        val keyInfo = keyIndex + wrappedKey
        val paddingLength = getPaddingAmount(keyInfo.size)
        val padding = ByteArray(paddingLength) { 0 }
        return MAGIC_BYTE_DATA + keyInfo.size.asTwoBytes() + keyInfo + padding
    }

    fun encrypt(data: ByteArray): ByteArray {
        val output = ByteArrayOutputStream()
        val aes = Cipher.getInstance("AES/CTR/NOPADDING")
        val hmac = createHmac()
        val fullHmac = createHmac()
        fullHmac.update(ByteArray(1) { 0x01 })
        val chunks = split(data, SEG_PAGE_SIZE)
        for ((index, chunk) in chunks.withIndex()) {
            val ivByteArray = ByteArray(SEG_IV_LENGTH)
            SecureRandom.getInstanceStrong().nextBytes(ivByteArray)
            output.write(ivByteArray)
            val iv = IvParameterSpec(ivByteArray + ByteArray(4) { 0 })
            aes.init(Cipher.ENCRYPT_MODE, aesKey, iv)
            val segmentIndex = index.toByteArray()

            val encrypted = aes.update(chunk)
            hmac.update(ivByteArray)
            hmac.update(segmentIndex)
            hmac.update(encrypted)
            val hash = hmac.doFinal()
            val truncatedMac = hash.copyOfRange(0, SEG_MAC_LENGTH)
            output.write(truncatedMac)
            fullHmac.update(truncatedMac)

            output.write(encrypted)
        }

        output.write(fullHmac.doFinal())

        val total = output.toByteArray()
        output.close()
        return total
    }

    private fun createHmac(): Mac {
        return Mac.getInstance("HmacSHA256").also {
            it.init(hmacKey)
        }
    }

    private fun getPaddingAmount(keyInfoLength: Int): Int {
        val totalLengthSoFar = MAGIC_BYTE_DATA.size + 2 + keyInfoLength
        return 16 - (totalLengthSoFar % 16)
    }

    private fun split(data: ByteArray, chunkSize: Int): List<ByteArray> {
        return if (data.size < chunkSize) {
            listOf(data)
        } else {
            listOf(data.copyOfRange(0, chunkSize)) + split(data.copyOfRange(chunkSize, data.size), chunkSize)
        }
    }

    companion object {
        internal fun fromKeySlot(key: Slot): FileEncryptor {
            val aesKey: SecretKey
            val hmacKey: SecretKey
            val keyInfo: ByteArray
            when (key.type) {
                SlotType.ACTIVE_AES_CTR_HMAC, SlotType.RETIRED_AES_CTR_HMAC -> {
                    aesKey = SecretKeySpec(key.data.copyOfRange(0, 16), "AES")
                    hmacKey = SecretKeySpec(key.data.copyOfRange(16, 32), "HmacSHA256")
                    keyInfo = ByteArray(0)
                }
                SlotType.ACTIVE_AES_WRAP, SlotType.RETIRED_AES_WRAP -> {
                    aesKey = KeyGenerator.getInstance("AES").generateKey()
                    hmacKey = KeyGenerator.getInstance("HmacSHA256").generateKey()
                    keyInfo =
                        wrapSecretKey(
                            aesKey.encoded + hmacKey.encoded,
                            key.data
                        )
                }
                else -> throw IllegalStateException()
            }

            return FileEncryptor(
                key.index,
                aesKey,
                hmacKey,
                keyInfo
            )
        }

        private fun wrapSecretKey(secretKey: ByteArray, encryptionKey: ByteArray): ByteArray {
            val aesWrap = Cipher.getInstance("AESWrap", "SunJCE")
            aesWrap.init(Cipher.WRAP_MODE, SecretKeySpec(encryptionKey, "AES"))
            return aesWrap.wrap(SecretKeySpec(secretKey, "AES/CTR/NOPADDING"))
        }
    }
}
