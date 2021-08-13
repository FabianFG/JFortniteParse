package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FStaticMeshSection {
    var materialIndex: Int
    var firstIndex : Int
    var numTriangles : Int
    var minVertexIndex : Int
    var maxVertexIndex : Int
    var enableCollision : Boolean
    var castShadow : Boolean
    var forceOpaque : Boolean
    var visibleInRayTracing : Boolean

    constructor(Ar: FArchive) {
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
        visibleInRayTracing = if (Ar.versions["StaticMesh.HasVisibleInRayTracing"])
            Ar.readBoolean()
        else
            false
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(materialIndex)
        Ar.writeInt32(firstIndex)
        Ar.writeInt32(numTriangles)
        Ar.writeInt32(minVertexIndex)
        Ar.writeInt32(maxVertexIndex)
        Ar.writeBoolean(enableCollision)
        Ar.writeBoolean(castShadow)
        if (Ar.game >= GAME_UE4(25))
            Ar.writeBoolean(forceOpaque)
    }

    constructor(
        materialIndex: Int,
        firstIndex: Int,
        numTriangles: Int,
        minVertexIndex: Int,
        maxVertexIndex: Int,
        enableCollision: Boolean,
        castShadow: Boolean,
        forceOpaque: Boolean,
        visibleInRayTracing : Boolean
    ) {
        this.materialIndex = materialIndex
        this.firstIndex = firstIndex
        this.numTriangles = numTriangles
        this.minVertexIndex = minVertexIndex
        this.maxVertexIndex = maxVertexIndex
        this.enableCollision = enableCollision
        this.castShadow = castShadow
        this.forceOpaque = forceOpaque
        this.visibleInRayTracing = visibleInRayTracing
    }
}