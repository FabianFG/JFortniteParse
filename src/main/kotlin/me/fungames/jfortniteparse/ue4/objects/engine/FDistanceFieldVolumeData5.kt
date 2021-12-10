package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox
import me.fungames.jfortniteparse.ue4.objects.core.math.FIntVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FSparseDistanceFieldMip {
    var indirectionDimensions: FIntVector
    var numDistanceFieldBricks: Int
    var volumeToVirtualUVScale: FVector
    var volumeToVirtualUVAdd: FVector
    var distanceFieldToVolumeScaleBias: FVector2D
    var bulkOffset: UInt
    var bulkSize: UInt

    constructor(Ar: FArchive) {
        indirectionDimensions = FIntVector(Ar)
        numDistanceFieldBricks = Ar.readInt32()
        volumeToVirtualUVScale = FVector(Ar)
        volumeToVirtualUVAdd = FVector(Ar)
        distanceFieldToVolumeScaleBias = FVector2D(Ar)
        bulkOffset = Ar.readUInt32()
        bulkSize = Ar.readUInt32()
    }
}

class FDistanceFieldVolumeData5 {
    var localSpaceMeshBounds: FBox
    var mostlyTwoSided: Boolean
    var mips: Array<FSparseDistanceFieldMip>
    var alwaysLoadedMip: ByteArray
    var streamableMips: FByteBulkData

    constructor(Ar: FAssetArchive) {
        localSpaceMeshBounds = FBox(Ar)
        mostlyTwoSided = Ar.readBoolean()
        mips = Array(3 /*DistanceField::NumMips*/) { FSparseDistanceFieldMip(Ar) }
        alwaysLoadedMip = Ar.read(Ar.readInt32())
        streamableMips = FByteBulkData(Ar)
    }
}