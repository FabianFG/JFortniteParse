package me.fungames.jfortniteparse.ue4.vfs

import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import java.io.Closeable
import java.nio.ByteBuffer

abstract class AbstractVfsReader(val path: String, val versions: VersionContainer) : Closeable {
    val name = path.replace('\\', '/').substringAfterLast('/')
    var files = emptyList<GameFile>()
        protected set
    val fileCount get() = files.size

    abstract val hasDirectoryIndex: Boolean
    var mountPoint = ""
        protected set
    var concurrent = false

    var game: Int
        inline get() = versions.game
        inline set(value) {
            versions.game = value
        }
    var ver: Int
        inline get() = versions.ver
        inline set(value) {
            versions.ver = value
        }

    abstract fun readIndex(): List<GameFile>

    fun extract(gameFile: GameFile) : ByteArray {
        val result = extractBuffer(gameFile)
        return if (result.hasArray())
            result.array()
        else {
            val buf = ByteArray(result.remaining())
            result[buf]
            buf
        }
    }

    abstract fun extractBuffer(gameFile: GameFile): ByteBuffer

    protected fun validateMountPoint(mountPoint: String): String {
        var badMountPoint = !mountPoint.startsWith("../../..")
        var mountPoint = mountPoint.substringAfter("../../..")
        if (mountPoint[0] != '/' || (mountPoint.length > 1 && mountPoint[1] == '.'))
            badMountPoint = true
        if (badMountPoint) {
            //PakFileReader.logger.warn("\"$name\" has strange mount point \"$mountPoint\", mounting to root")
            mountPoint = "/"
        }
        if (mountPoint.startsWith('/'))
            mountPoint = mountPoint.substring(1)
        return mountPoint
    }

    companion object {
        const val MAX_MOUNTPOINT_TEST_LENGTH = 128

        @JvmStatic inline fun isValidIndex(bytes: ByteArray) = isValidIndex(FByteArchive(bytes))
        @JvmStatic fun isValidIndex(reader: FByteArchive): Boolean {
            val stringLength = reader.readInt32()
            if (stringLength > MAX_MOUNTPOINT_TEST_LENGTH || stringLength < -MAX_MOUNTPOINT_TEST_LENGTH)
                return false
            // Calculate the pos of the null terminator for this string
            // Then read the null terminator byte and check whether it is actually 0
            return when {
                stringLength == 0 -> reader.readInt8() == 0.toByte()
                stringLength < 0 -> {
                    // UTF16
                    val nullTerminatorPos = 4 - (stringLength - 1) * 2
                    reader.seek(nullTerminatorPos)
                    reader.readInt16() == 0.toShort()
                }
                else -> {
                    // UTF8
                    val nullTerminatorPos = 4 + stringLength - 1
                    reader.seek(nullTerminatorPos)
                    reader.readInt8() == 0.toByte()
                }
            }
        }
    }

    abstract override fun close()

    override fun toString() = path
}