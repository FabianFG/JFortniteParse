package me.fungames.jfortniteparse.ue4.objects.engine.curves

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FRichCurveKey : UClass {
    var interpMode: Byte
    var tangentMode: Byte
    var tangentWeightMode: Byte
    var time: Float
    var arriveTangent: Float
    var arriveTangentWeight: Float
    var leaveTangent: Float
    var leaveTangentWeight: Float

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        interpMode = Ar.readInt8()
        tangentMode = Ar.readInt8()
        tangentWeightMode = Ar.readInt8()
        time = Ar.readFloat32()
        arriveTangent = Ar.readFloat32()
        arriveTangentWeight = Ar.readFloat32()
        leaveTangent = Ar.readFloat32()
        leaveTangentWeight = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt8(interpMode)
        Ar.writeInt8(tangentMode)
        Ar.writeInt8(tangentWeightMode)
        Ar.writeFloat32(time)
        Ar.writeFloat32(arriveTangent)
        Ar.writeFloat32(arriveTangentWeight)
        Ar.writeFloat32(leaveTangent)
        Ar.writeFloat32(leaveTangentWeight)
        super.completeWrite(Ar)
    }

    constructor(
        interpMode: Byte,
        tangentMode: Byte,
        tangentWeightMode: Byte,
        time: Float,
        arriveTangent: Float,
        arriveTangentWeight: Float,
        leaveTangent: Float,
        leaveTangentWeight: Float
    ) {
        this.interpMode = interpMode
        this.tangentMode = tangentMode
        this.tangentWeightMode = tangentWeightMode
        this.time = time
        this.arriveTangent = arriveTangent
        this.arriveTangentWeight = arriveTangentWeight
        this.leaveTangent = leaveTangent
        this.leaveTangentWeight = leaveTangentWeight
    }
}