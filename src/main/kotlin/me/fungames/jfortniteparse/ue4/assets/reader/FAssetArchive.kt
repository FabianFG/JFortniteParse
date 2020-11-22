package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.PakPackage
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import java.nio.ByteBuffer

/**
 * Binary reader for UE4 Assets
 */
open class FAssetArchive(data: ByteBuffer, val provider: FileProvider?, val pkgName: String) : FByteArchive(data) {
    constructor(data: ByteArray, provider: FileProvider?, pkgName: String) : this(ByteBuffer.wrap(data), provider, pkgName)

    // Asset Specific Fields
    lateinit var owner: Package
    protected var payloads = mutableMapOf<PayloadType, FAssetArchive>()
    var uassetSize = 0
    var uexpSize = 0
    var bulkDataStartOffset = 0

    open fun getPayload(type: PayloadType) = payloads[type] ?: throw ParserException("${type.name} is needed to parse the current package")
    fun addPayload(type: PayloadType, payload: FAssetArchive) {
        if (payloads.containsKey(type))
            throw ParserException("Can't add a payload that is already attached of type ${type.name}")
        payloads[type] = payload
    }

    override fun clone(): FAssetArchive {
        val c = FAssetArchive(data, provider, pkgName)
        c.littleEndian = littleEndian
        c.pos = pos
        payloads.forEach { c.payloads[it.key] = it.value }
        c.uassetSize = uassetSize
        c.uexpSize = uexpSize
        return c
    }

    fun seekRelative(pos: Int) {
        seek(pos - uassetSize - uexpSize)
    }

    fun relativePos() = uassetSize + uexpSize + pos()
    fun toNormalPos(relativePos: Int) = relativePos - uassetSize - uexpSize
    fun toRelativePos(normalPos: Int) = normalPos + uassetSize + uexpSize

    open fun handleBadNameIndex(nameIndex: Int) {
        throw ParserException("FName could not be read, requested index $nameIndex, name map size ${(owner as PakPackage).nameMap.size}", this)
    }

    override fun readFName(): FName {
        val owner = owner as PakPackage
        val nameIndex = this.readInt32()
        val extraIndex = this.readInt32()
        if (nameIndex in owner.nameMap.indices) {
            return FName(owner.nameMap, nameIndex, extraIndex)
        }
        handleBadNameIndex(nameIndex)
        return FName()
    }

    override fun printError() = "FAssetArchive Info: pos $pos, stopper $size, package $pkgName"
}