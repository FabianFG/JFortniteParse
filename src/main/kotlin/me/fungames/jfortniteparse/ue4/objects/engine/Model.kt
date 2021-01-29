package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.actors.ABrush
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent.FLightmassPrimitiveSettings
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FBoxSphereBounds
import me.fungames.jfortniteparse.ue4.objects.core.math.FPlane
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.engine.EBspNodeFlags.*
import me.fungames.jfortniteparse.ue4.reader.FArchive
import kotlin.jvm.internal.Ref.*

class FVert {
    /** Index of vertex. */
    val pVertex: Int
    /** If shared, index of unique side. Otherwise INDEX_NONE. */
    val iSide: Int

    /** The vertex's shadow map coordinate. */
    val shadowTexCoord: FVector2D

    /** The vertex's shadow map coordinate for the backface of the node. */
    val backfaceShadowTexCoord: FVector2D

    constructor(Ar: FArchive) {
        pVertex = Ar.readInt32()
        iSide = Ar.readInt32()
        shadowTexCoord = FVector2D(Ar)
        backfaceShadowTexCoord = FVector2D(Ar)
    }
}

enum class EBspNodeFlags(val value: UByte) {
    NF_NotCsg(0x01u), // Node is not a Csg splitter, i.e. is a transparent poly.
    NF_NotVisBlocking(0x04u), // Node does not block visibility, i.e. is an invisible collision hull.
    NF_BrightCorners(0x10u), // Temporary.
    NF_IsNew(0x20u), // Editor: Node was newly-added.
    NF_IsFront(0x40u), // Filter operation bounding-sphere precomputed and guaranteed to be front.
    NF_IsBack(0x80u), // Guaranteed back.
}

class FBspNode {
    // Persistent information.
    /** Plane the node falls into (X, Y, Z, W). */
    var plane: FPlane
    /** Index of first vertex in vertex pool, =iTerrain if NumVertices==0 and NF_TerrainFront. */
    var iVertPool: Int
    /** Index to surface information. */
    var iSurf: Int

    /** The index of the node's first vertex in the UModel's vertex buffer. */
    var iVertexIndex: Int

    /** The index in ULevel::ModelComponents of the UModelComponent containing this node. */
    var componentIndex: UShort

    /** The index of the node in the UModelComponent's Nodes array. */
    var componentNodeIndex: UShort

    /** The index of the element in the UModelComponent's Element array. */
    var componentElementIndex: Int

    /** Index to node in front (in direction of Normal). */
    var iBack: Int
    /** Index to node in back  (opposite direction as Normal). */
    var iFront: Int
    /** Index to next coplanar poly in coplanar list. */
    var iPlane: Int

    /** Collision bound. */
    var iCollisionBound: Int

    /** Visibility zone in 1=front, 0=back. */
    var iZone = UByteArray(2)
    /** Number of vertices in node. */
    var numVertices: UByte
    /** Node flags. */
    var nodeFlags: UByte
    /** Leaf in back and front, INDEX_NONE=not a leaf. */
    var iLeaf = IntArray(2)

    constructor(Ar: FArchive) {
        plane = FPlane(Ar)
        iVertPool = Ar.readInt32()
        iSurf = Ar.readInt32()
        iVertexIndex = Ar.readInt32()
        componentIndex = Ar.readUInt16()
        componentNodeIndex = Ar.readUInt16()
        componentElementIndex = Ar.readInt32()

        iBack = Ar.readInt32()
        iFront = Ar.readInt32()
        iPlane = Ar.readInt32()
        iCollisionBound = Ar.readInt32()
        iZone[0] = Ar.readUInt8()
        iZone[1] = Ar.readUInt8()
        numVertices = Ar.readUInt8()
        nodeFlags = Ar.readUInt8()
        iLeaf[0] = Ar.readInt32()
        iLeaf[1] = Ar.readInt32()
    }
}

/**
 * One Bsp polygon.  Lists all of the properties associated with the
 * polygon's plane.  Does not include a point list; the actual points
 * are stored along with Bsp nodes, since several nodes which lie in the
 * same plane may reference the same poly.
 */
class FBspSurf {
    /** Material. */
    var material: Lazy<UMaterialInterface>?
    /** Polygon flags. */
    var polyFlags: UInt
    /** Polygon & texture base point index (where U,V==0,0). */
    var pBase: Int
    /** Index to polygon normal. */
    var vNormal: Int
    /** Texture U-vector index. */
    var vTextureU: Int
    /** Texture V-vector index. */
    var vTextureV: Int
    /** Editor brush polygon index. */
    var iBrushPoly: Int
    /** Brush actor owning this Bsp surface. */
    var actor: Lazy<ABrush>?
    /** The plane this surface lies on. */
    var plane: FPlane
    /** The number of units/lightmap texel on this surface. */
    var lightMapScale: Float

    /** Index to the lightmass settings */
    var iLightmassIndex: Int

    constructor(Ar: FAssetArchive) {
        material = Ar.readObject()
        polyFlags = Ar.readUInt32()
        pBase = Ar.readInt32()
        vNormal = Ar.readInt32()
        vTextureU = Ar.readInt32()
        vTextureV = Ar.readInt32()
        iBrushPoly = Ar.readInt32()
        actor = Ar.readObject()
        plane = FPlane(Ar)
        lightMapScale = Ar.readFloat32()
        iLightmassIndex = Ar.readInt32()
    }
}

class UModel : UObject() {
    lateinit var nodes: Array<FBspNode>
    lateinit var verts: Array<FVert>
    lateinit var vectors: Array<FVector>
    lateinit var points: Array<FVector>
    lateinit var surfs: Array<FBspSurf>

    lateinit var lightmassSettings: Array<FLightmassPrimitiveSettings>

    /** A vertex buffer containing the vertices for all nodes in the UModel.  */
    //lateinit var vertexBuffer: FModelVertexBuffer

    /** The number of unique vertices.  */
    var numUniqueVertices = 0u

    /** Unique ID for this model, used for caching during distributed lighting  */
    lateinit var lightingGuid: FGuid

    // Other variables.
    var rootOutside = false
    var linked = false
    var numSharedSides = 0
    lateinit var bounds: FBoxSphereBounds

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        val stripVertexBufferFlag = 1
        val stripFlags = FStripDataFlags(Ar)
        bounds = FBoxSphereBounds(Ar)
        vectors = Ar.readBulkTArray { FVector(Ar) }
        points = Ar.readBulkTArray { FVector(Ar) }
        nodes = Ar.readBulkTArray { FBspNode(Ar) }
        nodes.forEach { it.nodeFlags = it.nodeFlags and (NF_IsNew.value or NF_IsFront.value or NF_IsBack.value).inv() }
        surfs = Ar.readTArray { FBspSurf(Ar) }
        verts = Ar.readBulkTArray { FVert(Ar) }
        numSharedSides = Ar.readInt32()
        rootOutside = Ar.readBoolean()
        linked = Ar.readBoolean()
        numUniqueVertices = Ar.readUInt32()
        // load/save vertex buffer
        if (!stripFlags.isEditorDataStripped() || !stripFlags.isClassDataStripped(stripVertexBufferFlag.toUByte())) {
            TODO() //vertexBuffer =
        }

        // serialize the lighting guid if it's there
        lightingGuid = FGuid(Ar)

        lightmassSettings = Ar.readTArray { FLightmassPrimitiveSettings(Ar) }
    }

    /**
     * Find Bsp node vertex nearest to a point (within a certain radius) and
     * set the location.  Returns distance, or -1.f if no point was found.
     */
    fun findNearestVertex(
        sourcePoint: FVector,
        destPoint: ObjectRef<FVector>,
        minRadius: FloatRef,
        pVertex: IntRef,
    ) = if (nodes.isNotEmpty()) findNearestVertex(this, sourcePoint, destPoint, minRadius, 0, pVertex) else -1f
}