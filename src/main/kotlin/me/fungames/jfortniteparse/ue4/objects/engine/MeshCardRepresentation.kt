package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.objects.core.math.FBox
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FLumenCardBuildData {
    var obb: FLumenCardOBB
    var lodLevel: UByte
    var axisAlignedDirectionIndex: UByte

    constructor(Ar: FArchive) {
        obb = FLumenCardOBB(Ar)
        lodLevel = Ar.readUInt8()
        axisAlignedDirectionIndex = Ar.readUInt8()
    }
}

class FLumenCardOBB {
    var origin: FVector
    var axisX: FVector
    var axisY: FVector
    var axisZ: FVector
    var extent: FVector

    constructor(Ar: FArchive) {
        origin = FVector(Ar)
        axisX = FVector(Ar)
        axisY = FVector(Ar)
        axisZ = FVector(Ar)
        extent = FVector(Ar)
    }
}

class FCardRepresentationData {
    var bounds: FBox
    var maxLodLevel: Int
    var cardBuildData: Array<FLumenCardBuildData>

    constructor(Ar: FArchive) {
        bounds = FBox(Ar)
        maxLodLevel = Ar.readInt32()
        cardBuildData = Ar.readTArray { FLumenCardBuildData(Ar) }
    }
}