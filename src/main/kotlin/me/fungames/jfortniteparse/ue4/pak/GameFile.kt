package me.fungames.jfortniteparse.ue4.pak

import me.fungames.jfortniteparse.ue4.pak.objects.FPakCompressedBlock
import me.fungames.jfortniteparse.ue4.pak.objects.FPakEntry
import me.fungames.jfortniteparse.util.div

@Suppress("EXPERIMENTAL_API_USAGE")
open class GameFile(val path: String = "", val pos: Long = 0L, val size: Long = 0L,
                    val uncompressedSize: Long = 0L,
                    val compressionMethod: CompressionMethod = CompressionMethod.None,
                    val compressedBlocks: Array<FPakCompressedBlock> = emptyArray(),
                    val compressionBlockSize: Int = 0,
                    val isEncrypted: Boolean = false,
                    val pakFileName: String
) {
    constructor(pakEntry: FPakEntry, mountPrefix : String, pakFileName : String) : this(
        mountPrefix / pakEntry.name, pakEntry.pos, pakEntry.size, pakEntry.uncompressedSize,
        pakEntry.compressionMethod, pakEntry.compressionBlocks, pakEntry.compressionBlockSize,
        pakEntry.isEncrypted, pakFileName
    )

    lateinit var uexp : GameFile
    var ubulk : GameFile? = null

    fun getExtension() = path.substringAfterLast('.')
    fun isUE4Package() = getExtension().run { this == "uasset" || this == "umap" }
    fun isLocres() = getExtension() == "locres"
    fun isAssetRegistry() = getName().run { startsWith("AssetRegistry") && endsWith(".bin") }

    fun hasUexp() = ::uexp.isInitialized
    fun hasUbulk() = ubulk != null

    fun isCompressed() = uncompressedSize != size || compressionMethod != CompressionMethod.None
    fun getPathWithoutExtension() = path.substringBeforeLast('.')
    fun getName() = path.substringAfterLast('/')
    fun getNameWithoutExtension() = getName().substringBeforeLast('.')

    override fun toString() = path

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameFile

        if (path != other.path) return false
        if (pos != other.pos) return false
        if (size != other.size) return false
        if (uncompressedSize != other.uncompressedSize) return false
        if (compressionMethod != other.compressionMethod) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + pos.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + uncompressedSize.hashCode()
        result = 31 * result + compressionMethod.hashCode()
        return result
    }
}