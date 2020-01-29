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
        fun fromPath(containerPath: Path): OmniStorage {
            val path = if(containerPath.fileName.toString().endsWith(".ofocus")) {
                containerPath
            } else {
                containerPath.resolve("OmniFocus.ofocus")
            }
            require(Files.exists(path)) { "Specified path does not exist." }
            require(Files.isDirectory(path)) { "Specified path is not a directory and thus cannot be an omnifocus storage path." }
            require(path.fileName.toString().endsWith(".ofocus")) { ".ofocus dir needs to be specified in path." }

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
