package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import org.slf4j.event.Level
import java.nio.ByteBuffer

class FExportArchive(data: ByteBuffer) : FAssetArchive(data, null, "") {
    lateinit var packageDesc: FAsyncPackageDesc2
    lateinit var importStore: FPackageImportStore
    lateinit var externalReadDependencies: List<FExternalReadCallback>
    lateinit var nameMap: FNameMap
    lateinit var exports: Array<FExportObject>
    lateinit var exportMap: Array<FExportMapEntry>
    var cookedHeaderSize = 0u
    var cookedSerialOffset = 0uL
    var cookedSerialSize = 0uL
    var bufferSerialOffset = 0uL

    fun handleBadNameIndex(nameIndex: Int) {
        asyncPackageLog(Level.ERROR, packageDesc, "HandleBadNameIndex",
            "Index: %d/%d".format(nameIndex, nameMap.size()))

        //name = FName()
        //setCriticalError()
    }

    override fun readFName(): FName {
        val nameIndex = readUInt32()
        val number = readUInt32()

        val mappedName = FMappedName.create(nameIndex, number, FMappedName.EType.Package)
        var name = nameMap.tryGetName(mappedName)
        if (name == null) {
            handleBadNameIndex(nameIndex.toInt())
            name = FName()
        }
        return name
    }
}