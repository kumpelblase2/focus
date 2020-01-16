package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.data.Changeset
import java.nio.file.Files
import java.nio.file.Path

interface OmniStorage {
    val location: Path
    val devices: Collection<OmniDevice>
    val capabilities: Collection<OmniCapability>
    val changeSets: List<Changeset>

    companion object {
        fun fromPath(path: Path): OmniStorage {
            require(Files.isDirectory(path))

            val encryptionFile =
                Files.list(path).filter { Files.isRegularFile(it) && it.fileName.toString() == "encrypted" }.findAny()
            return if (!encryptionFile.isPresent) {
                NormalStorage(path)
            } else {
                EncryptedStorage(path, encryptionFile.get())
            }
        }
    }
}

interface EncryptedOmniStorage : OmniStorage {
    fun providePassword(password: CharArray)
}
