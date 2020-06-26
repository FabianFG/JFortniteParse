package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_DEPRECATE_UMG_STYLE_ASSETS
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_RENAME_CROUCHMOVESCHARACTERDOWN
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FDistanceFieldVolumeData : UClass {
    var distanceFieldVolume : Array<Short> // TArray<Float16>
    var size : FIntVector
    var localBoundingBox : FBox
    var meshWasClosed : Boolean
    var builtAsIfTwoSided : Boolean
    var meshWasPlane : Boolean
    // 4.16+
    var compressedDistanceFieldVolume : Array<Byte>
    var distanceMinMax : FVector2D

    constructor(Ar : FArchive) {
        super.init(Ar)
        if (Ar.game >= GAME_UE4(16)) {
            compressedDistanceFieldVolume = Ar.readTArray { Ar.readInt8() }
            size = FIntVector(Ar)
            localBoundingBox = FBox(Ar)
            distanceMinMax = FVector2D(Ar)
            meshWasClosed = Ar.readBoolean()
            builtAsIfTwoSided = Ar.readBoolean()
            meshWasPlane = Ar.readBoolean()
            distanceFieldVolume = emptyArray()
        } else {
            distanceFieldVolume = Ar.readTArray { Ar.readInt16() }
            size = FIntVector(Ar)
            localBoundingBox = FBox(Ar)
            meshWasClosed = Ar.readBoolean()
            builtAsIfTwoSided = if (Ar.ver >= VER_UE4_RENAME_CROUCHMOVESCHARACTERDOWN)
                 Ar.readBoolean()
            else
                false
            meshWasPlane = if (Ar.ver >= VER_UE4_DEPRECATE_UMG_STYLE_ASSETS)
                 Ar.readBoolean()
            else
                false
            compressedDistanceFieldVolume = emptyArray()
            distanceMinMax = FVector2D(0f, 0f)
        }
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)

        super.completeWrite(Ar)
    }

    constructor(
        distanceFieldVolume: Array<Short>,
        size: FIntVector,
        localBoundingBox: FBox,
        meshWasClosed: Boolean,
        builtAsIfTwoSided: Boolean,
        meshWasPlane: Boolean,
        compressedDistanceFieldVolume: Array<Byte>,
        distanceMinMax: FVector2D
    ) {
        this.distanceFieldVolume = distanceFieldVolume
        this.size = size
        this.localBoundingBox = localBoundingBox
        this.meshWasClosed = meshWasClosed
        this.builtAsIfTwoSided = builtAsIfTwoSided
        this.meshWasPlane = meshWasPlane
        this.compressedDistanceFieldVolume = compressedDistanceFieldVolume
        this.distanceMinMax = distanceMinMax
    }
}