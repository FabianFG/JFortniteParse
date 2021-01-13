package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D
import me.fungames.jfortniteparse.ue4.objects.engine.FURL
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FPrecomputedVisibilityCell(Ar: FArchive) {
    val min = FVector(Ar)
    val chunkIndex = Ar.readUInt16()
    val dataOffset = Ar.readUInt16()
}

class FCompressedVisibilityChunk(Ar: FArchive) {
    val bCompressed = Ar.readBoolean()
    val uncompressedSize = Ar.readInt32()
    val data = Ar.read(Ar.readInt32())
}

class FPrecomputedVisibilityBucket(Ar: FArchive) {
    val cellDataSize = Ar.readInt32()
    val cells = Ar.readTArray { FPrecomputedVisibilityCell(Ar) }
    val cellDataChunks = Ar.readTArray { FCompressedVisibilityChunk(Ar) }
}

class FPrecomputedVisibilityHandler(Ar: FArchive) {
    val precomputedVisibilityCellBucketOriginXY = FVector2D(Ar)
    val precomputedVisibilityCellSizeXY = Ar.readFloat32()
    val precomputedVisibilityCellSizeZ = Ar.readFloat32()
    val precomputedVisibilityCellBucketSizeXY = Ar.readInt32()
    val precomputedVisibilityNumCellBuckets = Ar.readInt32()
    val precomputedVisibilityCellBuckets = Ar.readTArray { FPrecomputedVisibilityBucket(Ar) }
}

class FPrecomputedVolumeDistanceField(Ar: FArchive) {
    val volumeMaxDistance = Ar.readFloat32()
    val volumeBox = FBox(Ar)
    val volumeSizeX = Ar.readInt32()
    val volumeSizeY = Ar.readInt32()
    val volumeSizeZ = Ar.readInt32()
    val data = Ar.readTArray { FColor(Ar) }
}

@OnlyAnnotated
class ULevel : ULevel_Properties() {
    lateinit var url: FURL
    lateinit var actors: Array<Lazy<UObject>?> // Array<Lazy<AActor>?>
    var model: Lazy<UObject>? = null // UModel
    lateinit var modelComponents: Array<Lazy<UObject>?> // UModelComponent
    var levelScriptActor: Lazy<UObject>? = null // ALevelScriptActor
    var navListStart: Lazy<UObject>? = null // ANavigationObjectBase
    var navListEnd: Lazy<UObject>? = null // ANavigationObjectBase
    lateinit var precomputedVisibilityHandler: FPrecomputedVisibilityHandler
    lateinit var precomputedVolumeDistanceField: FPrecomputedVolumeDistanceField

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        actors = Ar.readTArray { Ar.readObject() }
        url = FURL(Ar)
        model = Ar.readObject()
        modelComponents = Ar.readTArray { Ar.readObject() }
        levelScriptActor = Ar.readObject()
        navListStart = Ar.readObject()
        navListEnd = Ar.readObject()
        precomputedVisibilityHandler = FPrecomputedVisibilityHandler(Ar)
        precomputedVolumeDistanceField = FPrecomputedVolumeDistanceField(Ar)
        super.complete(Ar)
    }
}
