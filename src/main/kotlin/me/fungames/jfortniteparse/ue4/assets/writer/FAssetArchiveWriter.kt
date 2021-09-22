package me.fungames.jfortniteparse.ue4.assets.writer

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectImport
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStream

open class FAssetArchiveWriter(val outputStream: OutputStream) : FArchiveWriter() {
    override var littleEndian = true

    protected var pos = 0

    //Asset Specific Fields
    lateinit var nameMap : MutableList<String>
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

    override fun writeFName(name: FName) {
        if (name.names.size == 1 && name.index == 0)
            return
        if (nameMap[name.index] != name.text) {
            throw ParserException("FName does not have a valid value, value in name map : ${nameMap[name.index]}, value in fname : ${name.text}", this)
        }
        writeInt32(name.index)
        writeInt32(name.number)
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

internal class FByteArchiveWriter() : FAssetArchiveWriter(ByteArrayOutputStream()) {

    val bos = super.outputStream as ByteArrayOutputStream

    fun toByteArray() = bos.toByteArray()

}