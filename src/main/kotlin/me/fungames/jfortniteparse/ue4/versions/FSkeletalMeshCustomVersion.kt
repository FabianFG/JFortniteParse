package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for SkeletalMesh types
object FSkeletalMeshCustomVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0
    // UE4.13 = 4
    // Remove Chunks array in FStaticLODModel and combine with Sections array
    const val CombineSectionWithChunk = 1
    // Remove FRigidSkinVertex and combine with FSoftSkinVertex array
    const val CombineSoftAndRigidVerts = 2
    // Need to recalc max bone influences
    const val RecalcMaxBoneInfluences = 3
    // Add NumVertices that can be accessed when stripping editor data
    const val SaveNumVertices = 4
    // UE4.14 = 5
    // Regenerated clothing section shadow flags from source sections
    const val RegenerateClothingShadowFlags = 5
    // UE4.15 = 7
    // Share color buffer structure with StaticMesh
    const val UseSharedColorBufferFormat = 6
    // Use separate buffer for skin weights
    const val UseSeparateSkinWeightBuffer = 7
    // UE4.16, UE4.17 = 9
    // Added new clothing systems
    const val NewClothingSystemAdded = 8
    // Cached inv mass data for clothing assets
    const val CachedClothInverseMasses = 9
    // UE4.18 = 10
    // Compact cloth vertex buffer, without dummy entries
    const val CompactClothVertexBuffer = 10
    // UE4.19 = 15
    // Remove SourceData
    const val RemoveSourceData = 11
    // Split data into Model and RenderData
    const val SplitModelAndRenderData = 12
    // Remove triangle sorting support
    const val RemoveTriangleSorting = 13
    // Remove the duplicated clothing sections that were a legacy holdover from when we didn't use our own render data
    const val RemoveDuplicatedClothingSections = 14
    // Remove 'Disabled' flag from SkelMesh asset sections
    const val DeprecateSectionDisabledFlag = 15
    // UE4.20-UE4.22 = 16
    // Add Section ignore by reduce
    const val SectionIgnoreByReduceAdded = 16
    // UE4.23-UE4.25 = 17
    // Adding skin weight profile support
    const val SkinWeightProfiles = 17 // TODO: FSkeletalMeshLODModel::Serialize (editor mesh)
    // UE4.26 = 18
    // Remove uninitialized/deprecated enable cloth LOD flag
    const val RemoveEnableClothLOD = 18 // TODO

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = RemoveEnableClothLOD

    @JvmField val GUID = FGuid(0xD78A4A00u, 0xE8584697u, 0xBAA819B5u, 0x487D46B4u)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE4(13) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(14) -> SaveNumVertices
            game < GAME_UE4(15) -> RegenerateClothingShadowFlags
            game < GAME_UE4(16) -> UseSeparateSkinWeightBuffer
            game < GAME_UE4(18) -> CachedClothInverseMasses
            game < GAME_UE4(19) -> CompactClothVertexBuffer
            game < GAME_UE4(20) -> DeprecateSectionDisabledFlag
            game < GAME_UE4(23) -> SectionIgnoreByReduceAdded
            game < GAME_UE4(26) -> SkinWeightProfiles
            else -> LatestVersion
        }
    }
}