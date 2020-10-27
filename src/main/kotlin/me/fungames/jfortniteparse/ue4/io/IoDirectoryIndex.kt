package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive

class FIoDirectoryIndexEntry {
    var name = 0u.inv()
    var firstChildEntry = 0u.inv()
    var nextSiblingEntry = 0u.inv()
    var firstFileEntry = 0u.inv()

    constructor(Ar: FArchive) {
        name = Ar.readUInt32()
        firstChildEntry = Ar.readUInt32()
        nextSiblingEntry = Ar.readUInt32()
        firstFileEntry = Ar.readUInt32()
    }
}

class FIoFileIndexEntry {
    var name = 0u.inv()
    var nextFileEntry = 0u.inv()
    var userData = 0u

    constructor(Ar: FArchive) {
        name = Ar.readUInt32()
        nextFileEntry = Ar.readUInt32()
        userData = Ar.readUInt32()
    }
}

class FIoDirectoryIndexResource {
    var mountPoint: String
    var directoryEntries: Array<FIoDirectoryIndexEntry>
    var fileEntries: Array<FIoFileIndexEntry>
    var stringTable: Array<String>

    constructor(Ar: FArchive) {
        mountPoint = Ar.readString()
        directoryEntries = Ar.readTArray { FIoDirectoryIndexEntry(Ar) }
        fileEntries = Ar.readTArray { FIoFileIndexEntry(Ar) }
        stringTable = Ar.readTArray { Ar.readString() }
    }
}

class FIoDirectoryIndexReaderImpl(buffer: ByteArray, decryptionKey: ByteArray) : FIoDirectoryIndexReader {
    var directoryIndex = buffer.run {
        if (buffer.isEmpty()) {
            throw FIoStatus.INVALID.toException()
        }
        FIoDirectoryIndexResource(FByteArchive(if (decryptionKey.size == 32) {
            Aes.decrypt(buffer, decryptionKey)
        } else this))
    }

    override fun getMountPoint() = directoryIndex.mountPoint

    override fun getChildDirectory(directory: FIoDirectoryIndexHandle) =
        if (directory.isValid() && isValidIndex()) {
            FIoDirectoryIndexHandle.fromIndex(getDirectoryEntry(directory).firstChildEntry)
        } else {
            FIoDirectoryIndexHandle.invalid()
        }

    override fun getNextDirectory(directory: FIoDirectoryIndexHandle) =
        if (directory.isValid() && isValidIndex()) {
            FIoDirectoryIndexHandle.fromIndex(getDirectoryEntry(directory).nextSiblingEntry)
        } else {
            FIoDirectoryIndexHandle.invalid()
        }

    override fun getFile(directory: FIoDirectoryIndexHandle) =
        if (directory.isValid() && isValidIndex()) {
            FIoDirectoryIndexHandle.fromIndex(getDirectoryEntry(directory).firstFileEntry)
        } else {
            FIoDirectoryIndexHandle.invalid()
        }

    override fun getNextFile(directory: FIoDirectoryIndexHandle) =
        if (directory.isValid() && isValidIndex()) {
            FIoDirectoryIndexHandle.fromIndex(getFileEntry(directory).nextFileEntry)
        } else {
            FIoDirectoryIndexHandle.invalid()
        }

    override fun getDirectoryName(directory: FIoDirectoryIndexHandle) =
        if (directory.isValid() && isValidIndex()) {
            val nameIndex = getDirectoryEntry(directory).name
            directoryIndex.stringTable[nameIndex.toInt()]
        } else {
            ""
        }

    override fun getFileName(file: FIoDirectoryIndexHandle) =
        if (file.isValid() && isValidIndex()) {
            val nameIndex = getFileEntry(file).name
            directoryIndex.stringTable[nameIndex.toInt()]
        } else {
            ""
        }

    override fun getFileData(file: FIoDirectoryIndexHandle) =
        if (file.isValid() && isValidIndex()) {
            directoryIndex.fileEntries[file.toIndex().toInt()].userData
        } else {
            0u.inv()
        }

    private fun getDirectoryEntry(directory: FIoDirectoryIndexHandle) = directoryIndex.directoryEntries[directory.toIndex().toInt()]

    private fun getFileEntry(file: FIoDirectoryIndexHandle) = directoryIndex.fileEntries[file.toIndex().toInt()]

    private fun isValidIndex() = directoryIndex.directoryEntries.isNotEmpty()
}