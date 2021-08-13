package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector4
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FReleaseObjectVersion

class FMeshToMeshVertData {
    var positionBaryCoordsAndDist: FVector4
    var normalBaryCoordsAndDist: FVector4
    var tangentBaryCoordsAndDist: FVector4
    var sourceMeshVertIndices: ShortArray
    var weight = 0f
    var padding: UInt

    constructor(Ar: FArchive) {
        positionBaryCoordsAndDist = FVector4(Ar)
        normalBaryCoordsAndDist = FVector4(Ar)
        tangentBaryCoordsAndDist = FVector4(Ar)
        sourceMeshVertIndices = ShortArray(4) { Ar.readInt16() }
        if (FReleaseObjectVersion.get(Ar) < FReleaseObjectVersion.WeightFMeshToMeshVertData) {
            // Old version had "uint32 Padding[2]"
            val discard = Ar.readUInt32()
            padding = Ar.readUInt32()
        } else {
            // New version has "float Weight and "uint32 Padding"
            weight = Ar.readFloat32()
            padding = Ar.readUInt32()
        }
    }
}