package me.fungames.jfortniteparse.ue4.pak.reader

import me.fungames.jfortniteparse.ue4.pak.objects.FPakInfo
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import me.fungames.jfortniteparse.ue4.versions.getArVer

abstract class FPakArchive(val fileName: String, versions: VersionContainer = VersionContainer.DEFAULT) : FArchive(versions) {
    val hasPakInfo: Boolean
        get() = ::pakInfo.isInitialized
    lateinit var pakInfo: FPakInfo

    public abstract override fun clone(): FPakArchive

    abstract fun seek(pos: Long)
    override fun seek(pos: Int) = seek(pos.toLong())

    abstract fun pakSize(): Long
    override fun size() = pakSize().toInt()

    abstract fun pakPos(): Long
    override fun pos() = pakPos().toInt()

    override fun printError() = "FPakArchive Info: pos ${pakPos()}, stopper ${pakSize()}"

    fun readAndCreateReader(size: Int): FBytePakArchive {
        if (this is FBytePakArchive)
            throw IllegalStateException("This is already a temp reader")
        val readerPos = pakPos()
        return FBytePakArchive(read(size), fileName, readerPos, pakSize())
    }

    fun createReader(data: ByteArray, offset: Long) =
        FBytePakArchive(data, fileName, offset, pakSize()).also { it.game = game; it.ver = getArVer(game) }
}