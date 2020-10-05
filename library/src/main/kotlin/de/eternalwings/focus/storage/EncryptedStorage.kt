package de.eternalwings.focus.storage

import de.eternalwings.focus.read
import de.eternalwings.focus.readExpecting
import de.eternalwings.focus.storage.data.Changeset
import de.eternalwings.focus.storage.data.ChangesetDescription
import de.eternalwings.focus.storage.encryption.*
import de.eternalwings.focus.storage.encryption.EncryptionConstants.AES_KEY_SIZE
import de.eternalwings.focus.storage.encryption.EncryptionConstants.FILE_MAC_LENGTH
import de.eternalwings.focus.storage.encryption.EncryptionConstants.HMAC_KEY_SIZE
import de.eternalwings.focus.storage.encryption.EncryptionConstants.MAGIC_BYTE_DATA
import de.eternalwings.focus.storage.plist.ArrayObject
import de.eternalwings.focus.storage.plist.DictionaryObject
import de.eternalwings.focus.storage.plist.Plist
import java.io.ByteArrayInputStream
import java.io.RandomAccessFile
import java.nio.file.Path
import java.util.zip.ZipInputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class EncryptedStorage(location: Path, encryptionPath: Path) : NormalStorage(location),
    EncryptedOmniStorage {
    private val encryptionInfo: EncryptionInfo =
        EncryptionInfo.fromPlist((Plist.parsePlist(encryptionPath) as ArrayObject).content.first() as DictionaryObject)

    private var unwrappedKeys: List<Slot> = emptyList()

    override fun providePassword(password: CharArray) {
        val keySpec = PBEKeySpec(password, encryptionInfo.salt, encryptionInfo.rounds, WRAPPER_KEY_LENGTH)
        val pbkdfKey = secretKeyFactory.generateSecret(keySpec)
        val unwrappedKeys = unwrapSecretKeyFrom(encryptionInfo.key, pbkdfKey.encoded)
        var currentIndex = 0
        val byteData = unwrappedKeys.encoded
        val secrets = emptyList<Slot>().toMutableList()
        while (currentIndex != byteData.size) {
            val type = SlotType.fromIndex(byteData[currentIndex].toInt())
            if (type == SlotType.NONE)
                break

            val slotLength = 4 * byteData[currentIndex + 1]
            val slotIdContent = byteData.copyOfRange(currentIndex + 2, currentIndex + 4)
            val slotId = slotIdContent.asTwoByteInt()
            val data = byteData.copyOfRange(currentIndex + 4, currentIndex + 4 + slotLength)
            secrets += Slot(type, slotId, data)
            currentIndex += 4 + slotLength
        }
        this.unwrappedKeys = secrets
    }

    private fun getSlotFromInfo(info: ByteArray): Slot {
        val keyId = info.copyOfRange(0, 2).asTwoByteInt()
        return unwrappedKeys.find { it.index == keyId } ?: throw IllegalStateException()
    }

    private fun getDecryptor(info: ByteArray): FileDecryptor {
        val slot = getSlotFromInfo(info)
        return when (slot.type) {
            SlotType.ACTIVE_AES_CTR_HMAC, SlotType.RETIRED_AES_CTR_HMAC -> {
                FileDecryptor(
                    slot.data.copyOfRange(0, AES_KEY_SIZE),
                    slot.data.copyOfRange(AES_KEY_SIZE, AES_KEY_SIZE + HMAC_KEY_SIZE)
                )
            }
            SlotType.ACTIVE_AES_WRAP, SlotType.RETIRED_AES_WRAP -> {
                val wrappedKey = info.copyOfRange(2, info.size)
                val unwrapped = unwrapSecretKeyFrom(wrappedKey, slot.data)
                FileDecryptor(
                    unwrapped.encoded.copyOfRange(0, AES_KEY_SIZE),
                    unwrapped.encoded.copyOfRange(AES_KEY_SIZE, AES_KEY_SIZE + HMAC_KEY_SIZE)
                )
            }
            else -> throw IllegalStateException()
        }
    }

    private fun unwrapSecretKeyFrom(wrappedKey: ByteArray, decryptionKey: ByteArray): SecretKey {
        val aesWrap = Cipher.getInstance("AESWrap", "SunJCE")
        aesWrap.init(Cipher.UNWRAP_MODE, SecretKeySpec(decryptionKey, "AES"))
        return aesWrap.unwrap(wrappedKey, "AES/CTR/NOPADDING", Cipher.SECRET_KEY) as SecretKey
    }

    override fun getChangesetFor(description: ChangesetDescription): Changeset {
        check(unwrappedKeys.isNotEmpty()) { "No password was provided for encrypted store." }
        return super.getChangesetFor(description)
    }

    override fun unencryptedCopy(): OmniStorage {
        return InMemoryStorage(devices, capabilities, changeSets)
    }

    override fun getContentOfFile(file: Path): ZipInputStream {
        val content = RandomAccessFile(file.toFile(), "r").use {
            check(it.readExpecting(MAGIC_BYTE_DATA))
            val keyInfoLength = it.readUnsignedShort()
            val keyInfo = it.read(keyInfoLength)
            val paddingLength = getPaddingAmount(keyInfoLength)
            val padding = it.read(paddingLength)
            check(padding.all { byte -> byte == 0.toByte() })
            val decryptor = getDecryptor(keyInfo)
            val segmentStart = it.filePointer.toInt()
            it.seek(it.length() - FILE_MAC_LENGTH)
            val segmentEnd = it.filePointer.toInt()
            val fileHMAC = it.read(FILE_MAC_LENGTH)
            decryptor.checkHMAC(it, segmentStart, segmentEnd, fileHMAC)
            decryptor.decrypt(it, segmentStart, segmentEnd)
        }

        return ZipInputStream(ByteArrayInputStream(content))
    }

    override fun createChangesetFile(filename: String, output: ByteArray) {
        check(unwrappedKeys.isNotEmpty()) { "No password was provided for encrypted store." }
        val encryptor = FileEncryptor.fromKeySlot(unwrappedKeys.first())
        val preamble = encryptor.createPreamble()
        super.createChangesetFile(filename, preamble + encryptor.encrypt(output))
    }

    private fun getPaddingAmount(keyInfoLength: Int): Int {
        val totalLengthSoFar = MAGIC_BYTE_DATA.size + 2 + keyInfoLength
        return 16 - (totalLengthSoFar % 16)
    }

    companion object {
        private val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        private const val WRAPPER_KEY_LENGTH = 128
    }
}
