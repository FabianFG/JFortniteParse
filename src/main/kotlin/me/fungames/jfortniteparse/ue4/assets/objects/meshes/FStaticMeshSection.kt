package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_25
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FStaticMeshSection : UClass {
    var materialIndex: Int
    var firstIndex : Int
    var numTriangles : Int
    var minVertexIndex : Int
    var maxVertexIndex : Int
    var enableCollision : Boolean
    var castShadow : Boolean
    var forceOpaque : Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        materialIndex = Ar.readInt32()
        firstIndex = Ar.readInt32()
        numTriangles = Ar.readInt32()
        minVertexIndex = Ar.readInt32()
        maxVertexIndex = Ar.readInt32()
        enableCollision = Ar.readBoolean()
        castShadow = Ar.readBoolean()
        forceOpaque = if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.StaticMeshSectionForceOpaqueField)
            Ar.readBoolean()
        else
            false
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(materialIndex)
        Ar.writeInt32(firstIndex)
        Ar.writeInt32(numTriangles)
        Ar.writeInt32(minVertexIndex)
        Ar.writeInt32(maxVertexIndex)
        Ar.writeBoolean(enableCollision)
        Ar.writeBoolean(castShadow)
        if (Ar.game >= GAME_UE4_25)
            Ar.writeBoolean(forceOpaque)
        super.completeWrite(Ar)
    }

    constructor(
        materialIndex: Int,
        firstIndex: Int,
        numTriangles: Int,
        minVertexIndex: Int,
        maxVertexIndex: Int,
        enableCollision: Boolean,
        castShadow: Boolean,
        forceOpague: Boolean
    ) {
        this.materialIndex = materialIndex
        this.firstIndex = firstIndex
        this.numTriangles = numTriangles
        this.minVertexIndex = minVertexIndex
        this.maxVertexIndex = maxVertexIndex
        this.enableCollision = enableCollision
        this.castShadow = castShadow
        this.forceOpaque = forceOpague
    }
}