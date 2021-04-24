package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@UStruct
class FBoxSphereBounds {
    @JvmField @UProperty("Origin")
    var origin: FVector
    @JvmField @UProperty("BoxExtent")
    var boxExtent: FVector
    @JvmField @UProperty("SphereRadius")
    var sphereRadius: Float

    constructor(Ar: FArchive) {
        origin = FVector(Ar)
        boxExtent = FVector(Ar)
        sphereRadius = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        origin.serialize(Ar)
        boxExtent.serialize(Ar)
        Ar.writeFloat32(sphereRadius)
    }

    constructor(origin: FVector, boxExtent: FVector, sphereRadius: Float) {
        this.origin = origin
        this.boxExtent = boxExtent
        this.sphereRadius = sphereRadius
    }
}