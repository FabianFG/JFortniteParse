package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector4
import me.fungames.jfortniteparse.ue4.reader.FArchive


class FPackedHierarchyNode {
    companion object {
        const val MAX_BVH_NODE_FANOUT_BITS = 3
        const val MAX_BVH_NODE_FANOUT = 1 shl MAX_BVH_NODE_FANOUT_BITS
    }

    var lodBounds: Array<FVector4>
    var misc0: Array<FMisc0>
    var misc1: Array<FMisc1>
    var misc2: Array<FMisc2>

    constructor(Ar: FArchive) {
        lodBounds = Array(MAX_BVH_NODE_FANOUT) { FVector4(Ar) }
        misc0 = Array(MAX_BVH_NODE_FANOUT) { FMisc0(Ar) }
        misc1 = Array(MAX_BVH_NODE_FANOUT) { FMisc1(Ar) }
        misc2 = Array(MAX_BVH_NODE_FANOUT) { FMisc2(Ar) }
    }

    class FMisc0 {
        var boxBoundsCenter: FVector
        var minLODError_MaxParentLODError: UInt

        constructor(Ar: FArchive) {
            boxBoundsCenter = FVector(Ar)
            minLODError_MaxParentLODError = Ar.readUInt32()
        }
    }

    class FMisc1 {
        var boxBoundsExtent: FVector
        var childStartReference: UInt

        constructor(Ar: FArchive) {
            boxBoundsExtent = FVector(Ar)
            childStartReference = Ar.readUInt32()
        }
    }

    class FMisc2 {
        var resourcePageIndex_NumPages_GroupPartSize: UInt

        constructor(Ar: FArchive) {
            resourcePageIndex_NumPages_GroupPartSize = Ar.readUInt32()
        }
    }
}

class FPageStreamingState {
    var bulkOffset: UInt
    var bulkSize: UInt
    var pageSize: UInt
    var dependenciesStart: UInt
    var dependenciesNum: UInt
    var flags: UInt

    constructor(Ar: FArchive) {
        bulkOffset = Ar.readUInt32()
        bulkSize = Ar.readUInt32()
        pageSize = Ar.readUInt32()
        dependenciesStart = Ar.readUInt32()
        dependenciesNum = Ar.readUInt32()
        flags = Ar.readUInt32()
    }
}

class FNaniteResources {
    // Persistent State
    var rootClusterPage: ByteArray // Root page is loaded on resource load, so we always have something to draw.
    var streamableClusterPages: FByteBulkData // Remaining pages are streamed on demand.
    var imposterAtlas: Array<UShort>
    var hierarchyNodes: Array<FPackedHierarchyNode>
    var hierarchyRootOffsets: Array<UInt>
    var pageStreamingStates: Array<FPageStreamingState>
    var pageDependencies: Array<UInt>
    var positionPrecision: Int
    var numInputTriangles: UInt
    var numInputVertices: UInt
    var numInputMeshes: UShort
    var numInputTexCoords: UShort
    var resourceFlags: UInt

    constructor(Ar: FAssetArchive) {
        resourceFlags = Ar.readUInt32()
        rootClusterPage = Ar.read(Ar.readInt32())
        streamableClusterPages = FByteBulkData(Ar)
        pageStreamingStates = Ar.readTArray { FPageStreamingState(Ar) }

        hierarchyNodes = Ar.readTArray { FPackedHierarchyNode(Ar) }
        hierarchyRootOffsets = Ar.readTArray { Ar.readUInt32() }
        pageDependencies = Ar.readTArray { Ar.readUInt32() }
        imposterAtlas = Ar.readTArray { Ar.readUInt16() }
        positionPrecision = Ar.readInt32()
        numInputTriangles = Ar.readUInt32()
        numInputVertices = Ar.readUInt32()
        numInputMeshes = Ar.readUInt16()
        numInputTexCoords = Ar.readUInt16()
    }
}