package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.div

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
        mountPoint = Ar.readString().substringAfter("../../../")
        directoryEntries = Ar.readTArray { FIoDirectoryIndexEntry(Ar) }
        fileEntries = Ar.readTArray { FIoFileIndexEntry(Ar) }
        stringTable = Ar.readTArray { Ar.readString() }
    }
}

class FIoDirectoryIndexReaderImpl(buffer: ByteArray, decryptionKey: ByteArray?) : FIoDirectoryIndexReader {
    var directoryIndex = buffer.let {
        if (it.isEmpty()) {
            throw FIoStatus.INVALID.toException()
        }
        if (decryptionKey != null) {
            Aes.decryptData(it, decryptionKey)
        }
        FIoDirectoryIndexResource(FByteArchive(it))
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

    override fun iterateDirectoryIndex(directoryIndexHandle: FIoDirectoryIndexHandle, path: String, visit: FDirectoryIndexVisitorFunction): Boolean {
        var file = getFile(directoryIndexHandle)
        while (file.isValid()) {
            val tocEntryIndex = getFileData(file)
            val fileName = getFileName(file)
            val filePath = getMountPoint() / path / fileName

            if (!visit(filePath, tocEntryIndex)) {
                return false
            }

            file = getNextFile(file)
        }

        var childDirectory = getChildDirectory(directoryIndexHandle)
        while (childDirectory.isValid()) {
            val directoryName = getDirectoryName(childDirectory)
            val childDirectoryPath = path / directoryName

            if (!iterateDirectoryIndex(childDirectory, childDirectoryPath, visit)) {
                return false
            }

            childDirectory = getNextDirectory(childDirectory)
        }

        return true
    }

    private fun getDirectoryEntry(directory: FIoDirectoryIndexHandle) = directoryIndex.directoryEntries[directory.toIndex().toInt()]

    private fun getFileEntry(file: FIoDirectoryIndexHandle) = directoryIndex.fileEntries[file.toIndex().toInt()]

    private fun isValidIndex() = directoryIndex.directoryEntries.isNotEmpty()
}