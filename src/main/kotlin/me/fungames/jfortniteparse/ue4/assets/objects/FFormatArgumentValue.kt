package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EFormatArgumentType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.awt.Color

@ExperimentalUnsignedTypes
class FFormatArgumentValue : UClass {
    var type : EFormatArgumentType
    var value : Any

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        type = EFormatArgumentType.values()[Ar.readInt8().toInt()]
        value = when(type) {
            EFormatArgumentType.Int -> Ar.readInt64()
            EFormatArgumentType.UInt -> Ar.readUInt64()
            EFormatArgumentType.Float -> Ar.readFloat32()
            EFormatArgumentType.Double -> Ar.readDouble()
            EFormatArgumentType.Text -> FText(Ar)
            EFormatArgumentType.Gender -> TODO("Gender Argument not supported yet")
        }
        super.complete(Ar)
    }

    constructor(type: EFormatArgumentType, value: Any) {
        this.type = type
        this.value = value
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt8(type.ordinal.toByte())
        when(type) {
            EFormatArgumentType.Int -> Ar.writeInt64(value as Long)
            EFormatArgumentType.UInt -> Ar.writeUInt64(value as ULong)
            EFormatArgumentType.Float -> Ar.writeFloat32(value as Float)
            EFormatArgumentType.Double -> Ar.writeDouble(value as Double)
            EFormatArgumentType.Text -> (value as FText).serialize(Ar)
            EFormatArgumentType.Gender -> TODO("Gender Argument not supported yet")
        }
        super.completeWrite(Ar)
    }


}