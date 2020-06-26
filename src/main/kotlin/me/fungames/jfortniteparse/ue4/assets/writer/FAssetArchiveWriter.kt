package me.fungames.jfortniteparse.ue4.assets.writer

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.objects.FNameEntry
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectImport
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@ExperimentalUnsignedTypes
open class FAssetArchiveWriter(val outputStream: OutputStream) : FArchiveWriter() {
    override var littleEndian = true

    protected var pos = 0

    //Asset Specific Fields
    lateinit var nameMap : MutableList<FNameEntry>
    lateinit var importMap : MutableList<FObjectImport>
    lateinit var exportMap : MutableList<FObjectExport>

    private var payloads = mutableMapOf<PayloadType, FAssetArchiveWriter>()

    fun getPayload(type: PayloadType) = payloads[type] ?: throw ParserException("${type.name} is needed to write the current package")
    fun addPayload(type: PayloadType, payload : FAssetArchiveWriter) {
        if (payloads.containsKey(type))
            throw ParserException("Can't add a payload that is already attached of type ${type.name}")
        payloads[type] = payload
    }


    var uassetSize = 0
    var uexpSize = 0

    override fun pos() = pos

    fun relativePos() = uassetSize + uexpSize + pos()
    fun toNormalPos(relativePos : Int) = relativePos - uassetSize - uexpSize
    fun toRelativePos(normalPos : Int) = normalPos + uassetSize + uexpSize

    fun writeFName(i : FName) {
        if (i is FName.FNameDummy)
            return
        if (nameMap[i.index].name != i.text) {
            throw ParserException("FName does not have a valid value, value in name map : ${nameMap[i.index].name}, value in fname : ${i.text}", this)
        }
        writeInt32(i.index)
        writeInt32(i.extraIndex)
    }

    override fun write(buffer: ByteArray) {
        outputStream.write(buffer)
        pos += buffer.size
    }

    internal fun setupByteArrayWriter() : FByteArchiveWriter {
        val ar = FByteArchiveWriter()
        ar.uassetSize = uassetSize
        ar.uexpSize = uexpSize
        payloads.forEach { ar.addPayload(it.key, it.value) }
        ar.nameMap = nameMap
        ar.importMap = importMap
        ar.exportMap = exportMap
        ar.pos = pos()
        return ar
    }

    override fun printError() = "FAssetArchiveWriter Info: pos $pos"
}

@ExperimentalUnsignedTypes
internal class FByteArchiveWriter() : FAssetArchiveWriter(ByteArrayOutputStream()) {

    val bos = super.outputStream as ByteArrayOutputStream

    fun toByteArray() = bos.toByteArray()

}