package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.ue4.assets.objects.FNameEntry
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectImport
import me.fungames.jfortniteparse.ue4.assets.objects.FPackageFileSummary
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.assets.Package

/**
 * Binary reader for UE4 Assets
 */
@ExperimentalUnsignedTypes
class FAssetArchive(data : ByteArray, private val provider: FileProvider?) : FByteArchive(data) {

    //Asset Specific Fields
    lateinit var nameMap : MutableList<FNameEntry>
    lateinit var importMap : MutableList<FObjectImport>
    lateinit var exportMap : MutableList<FObjectExport>


    private val importCache = mutableMapOf<String, Package>()

    private var payloads = mutableMapOf<PayloadType, FAssetArchive>()
    var uassetSize = 0
    var uexpSize = 0
    var info : FPackageFileSummary? = null

    fun getPayload(type: PayloadType) = payloads[type] ?: throw ParserException("${type.name} is needed to parse the current package")
    fun addPayload(type: PayloadType, payload : FAssetArchive) {
        if (payloads.containsKey(type))
            throw ParserException("Can't add a payload that is already attached of type ${type.name}")
        payloads[type] = payload
    }

    override fun clone(): FAssetArchive {
        val c = FAssetArchive(data, provider)
        c.littleEndian = littleEndian
        c.pos = pos
        payloads.forEach { c.payloads[it.key] = it.value }
        c.uassetSize = uassetSize
        c.uexpSize = uexpSize
        return c
    }

    fun seekRelative(pos : Int) {
        seek(pos - uassetSize - uexpSize)
    }

    fun relativePos() = uassetSize + uexpSize + pos()
    fun toNormalPos(relativePos : Int) = relativePos - uassetSize - uexpSize
    fun toRelativePos(normalPos : Int) = normalPos + uassetSize + uexpSize

    fun readFName() : FName {
        val nameIndex = this.readInt32()
        val nameNumber = this.readInt32() // name number?
        if (nameIndex in nameMap.indices)
            return FName(nameMap, nameIndex, nameNumber)
        else
            throw ParserException("FName could not be read, requested index $nameIndex, name map size ${nameMap.size}", this)
    }

    fun loadImport(path : String) : Package? {
        if (provider == null) return null
        val fixedPath = provider.fixPath(path)
        val cachedPackage = importCache[fixedPath]
        if (cachedPackage != null)
            return cachedPackage
        val pkg = provider.loadGameFile(path)
        return if (pkg != null) {
            importCache[fixedPath] = pkg
            pkg
        } else null
    }

    fun clearImportCache() = importCache.clear()

    override fun printError() = "FAssetArchive Info: pos $pos, stopper $size"
}