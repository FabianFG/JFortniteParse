package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FByteBulkData : UClass {
    var header : FByteBulkDataHeader
    var data : ByteArray

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        header = FByteBulkDataHeader(Ar)
        val bulkDataFlags = header.bulkDataFlags
        data = ByteArray(header.elementCount)
        when {
            EBulkData.BULKDATA_Unused.check(bulkDataFlags) -> {
                logger.warn("Bulk with no data")
            }
            EBulkData.BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                logger.debug("bulk data in .uexp file (Force Inline Payload) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                Ar.read(data)
            }
            EBulkData.BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                logger.debug("bulk data in .ubulk file (Payload In Seperate File) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                val ubulkAr = Ar.getPayload(PayloadType.UBULK)
                ubulkAr.seek(header.offsetInFile.toInt())
                ubulkAr.read(data)
            }
            EBulkData.BULKDATA_OptionalPayload.check(bulkDataFlags) -> {
                throw ParserException("TODO: Uptnl", Ar)
            }
            EBulkData.BULKDATA_PayloadAtEndOfFile.check(bulkDataFlags) -> {
                //stored in same file, but at different position
                //save archive position
                val savePos = Ar.pos()
                if (header.offsetInFile.toInt() + header.elementCount <= Ar.size()) {
                    Ar.seek(header.offsetInFile.toInt())
                    Ar.read(data)
                } else {
                    throw ParserException("Failed to read PayloadAtEndOfFile, ${header.offsetInFile} is out of range", Ar)
                }
                Ar.seek(savePos)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        val bulkDataFlags = header.bulkDataFlags
        when {
            EBulkData.BULKDATA_Unused.check(bulkDataFlags) -> {
                header.serialize(Ar)
            }
            EBulkData.BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                header.offsetInFile = (Ar.relativePos() + 28).toLong()
                header.elementCount = data.size
                header.sizeOnDisk = data.size
                header.serialize(Ar)
                Ar.write(data)
            }
            EBulkData.BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                val ubulkAr = Ar.getPayload(PayloadType.UBULK)
                header.offsetInFile = ubulkAr.relativePos().toLong()
                header.elementCount = data.size
                header.sizeOnDisk = data.size
                header.serialize(Ar)
                ubulkAr.write(data)
            }
            EBulkData.BULKDATA_OptionalPayload.check(bulkDataFlags) -> {
                throw ParserException("TODO: Uptnl")
            }
            else -> throw ParserException("Unsupported BulkData type $bulkDataFlags")
        }
        super.completeWrite(Ar)
    }

    constructor(header : FByteBulkDataHeader, data : ByteArray) {
        this.header = header
        this.data = data
    }
}