package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FBoxSphereBounds : UClass {
    var origin : FVector
    var boxExtent : FVector
    var sphereRadius : Float

    constructor(Ar : FArchive) {
        super.init(Ar)
        origin = FVector(Ar)
        boxExtent = FVector(Ar)
        sphereRadius = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        origin.serialize(Ar)
        boxExtent.serialize(Ar)
        Ar.writeFloat32(sphereRadius)
        super.completeWrite(Ar)
    }

    constructor(origin: FVector, boxExtent: FVector, sphereRadius: Float) {
        this.origin = origin
        this.boxExtent = boxExtent
        this.sphereRadius = sphereRadius
    }
}