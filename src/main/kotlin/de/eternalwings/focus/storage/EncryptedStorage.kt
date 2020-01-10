package de.eternalwings.focus.storage

import de.eternalwings.focus.read
import de.eternalwings.focus.readExpecting
import de.eternalwings.plist.ArrayObject
import de.eternalwings.plist.DictionaryObject
import de.eternalwings.plist.Plist
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.file.Path
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

internal data class Slot(val type: SlotType, val index: Int, val data: ByteArray)

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
            return SlotType.values().find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}

class EncryptedStorage(location: Path, encryptionPath: Path) : NormalStorage(location),
    EncryptedOmniStorage {
    private val encryptionInfo: EncryptionInfo =
        EncryptionInfo.fromPlist((Plist.parsePlist(encryptionPath) as ArrayObject).content.first() as DictionaryObject)

    private var unwrappedKeys: List<Slot> = emptyList()

    override fun providePassword(password: CharArray) {
        val keySpec = PBEKeySpec(password, encryptionInfo.salt, encryptionInfo.rounds, WRAPPER_KEY_LENGTH)
        val pbkdfKey = secretKeyFactory.generateSecret(keySpec)
        val aesWrap = Cipher.getInstance("AESWrap", "SunJCE")
        aesWrap.init(Cipher.UNWRAP_MODE, SecretKeySpec(pbkdfKey.encoded, "AES"))
        val unwrappedKeys = aesWrap.unwrap(encryptionInfo.key, "AES/CTR/NOPADDING", Cipher.SECRET_KEY) as SecretKey
        var currentIndex = 0
        val byteData = unwrappedKeys.encoded
        val secrets = emptyList<Slot>().toMutableList()
        while (currentIndex != byteData.size) {
            val type = SlotType.fromIndex(byteData[currentIndex].toInt())
            if (type == SlotType.NONE)
                break

            val slotLength = 4 * byteData[currentIndex + 1]
            val slotIdContent = byteData.copyOfRange(currentIndex + 2, currentIndex + 4)
            val slotId = (slotIdContent[1].toInt() shl 8) or slotIdContent[0].toInt()
            val data = byteData.copyOfRange(currentIndex + 4, currentIndex + 4 + slotLength)
            secrets += Slot(type, slotId, data)
            currentIndex += 4 + slotLength
        }
        this.unwrappedKeys = secrets
    }

    private fun getSlotFromInfo(info: ByteArray): Slot {
        val keyId = info.copyOfRange(0, 2).toTwoByteInt()
        return unwrappedKeys.find { it.index == keyId } ?: throw IllegalStateException()
    }

    private fun getDecryptor(info: ByteArray): FileDecryptor {
        val slot = getSlotFromInfo(info)
        return when (slot.type) {
            SlotType.ACTIVE_AES_CTR_HMAC, SlotType.RETIRED_AES_CTR_HMAC -> {
                FileDecryptor(slot.data.copyOfRange(0, 16), slot.data.copyOfRange(16, 32))
            }
            SlotType.ACTIVE_AES_WRAP, SlotType.RETIRED_AES_WRAP -> {
                val wrappedKey = info.copyOfRange(2, info.size)
                val aesWrap = Cipher.getInstance("AESWrap", "SunJCE")
                aesWrap.init(Cipher.UNWRAP_MODE, SecretKeySpec(slot.data, "AES"))
                val unwrapped = aesWrap.unwrap(wrappedKey, "AES/CTR/NOPADDING", Cipher.SECRET_KEY) as SecretKey
                FileDecryptor(unwrapped.encoded.copyOfRange(0, 16), unwrapped.encoded.copyOfRange(16, 32))
            }
            else -> throw IllegalStateException()
        }
    }

    override fun getContentOfFile(file: Path): ZipInputStream {
        val content = RandomAccessFile(file.toFile(), "r").use {
            val magic = MAGIC.toByteArray() + ByteArray(2) { 0 }
            check(it.readExpecting(magic))
            val keyInfoLength = it.readUnsignedShort()
            val keyInfo = it.read(keyInfoLength)
            val offset = magic.size + 2 + keyInfoLength
            val paddingLength = 16 - (offset % 16)
            val padding = it.read(paddingLength)
            check(padding.all { byte -> byte == 0.toByte() })
            val decryptor = getDecryptor(keyInfo)
            val segmentStart = it.filePointer.toInt()
            it.seek(it.length() - FileDecryptor.FILE_MAC_LENGTH)
            val segmentEnd = it.filePointer.toInt()
            val fileHMAC = it.read(FileDecryptor.FILE_MAC_LENGTH)
            decryptor.checkHMAC(it, segmentStart, segmentEnd, fileHMAC)
            decryptor.decrypt(it, segmentStart, segmentEnd)
        }

        return ZipInputStream(ByteArrayInputStream(content))
    }

    private fun ByteArray.toTwoByteInt(): Int {
        require(this.size == 2)
        return (this[1].toInt() shl 8) or this[0].toInt()
    }

    companion object {
        private val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        private val aes = Cipher.getInstance("AES")
        private const val WRAPPER_KEY_LENGTH = 128
        private const val MAGIC = "OmniFileEncryption"
    }
}
