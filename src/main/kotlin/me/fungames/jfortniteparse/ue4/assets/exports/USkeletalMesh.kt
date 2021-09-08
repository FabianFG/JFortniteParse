package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVChannelInfo
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FReferenceSkeleton
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FSkeletalMeshLODModel
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FBoxSphereBounds
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.EPackageFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.*
import kotlin.jvm.JvmField as F

class FSkeletalMaterial {
    var material: Lazy<UMaterialInterface>?
    var enableShadowCasting = false
    var materialSlotName = FName.NAME_None
    var uvChannelData: FMeshUVChannelInfo? = null

    constructor(Ar: FAssetArchive) {
        material = Ar.readObject()
        if (FEditorObjectVersion.get(Ar) >= FEditorObjectVersion.RefactorMeshEditorMaterials) {
            materialSlotName = Ar.readFName()
            var serializeImportedMaterialSlotName = Ar.owner.packageFlags and EPackageFlags.PKG_FilterEditorOnly.value == 0
            if (FCoreObjectVersion.get(Ar) >= FCoreObjectVersion.SkeletalMaterialEditorDataStripping) {
                serializeImportedMaterialSlotName = Ar.readBoolean()
            }
            if (serializeImportedMaterialSlotName) {
                val importedMaterialSlotName = Ar.readFName()
            }
        } else {
            if (Ar.ver >= VER_UE4_MOVE_SKELETALMESH_SHADOWCASTING) {
                enableShadowCasting = true
            }
            if (FRecomputeTangentCustomVersion.get(Ar) >= FRecomputeTangentCustomVersion.RuntimeRecomputeTangent) {
                val recomputeTangent = Ar.readBoolean()
            }
        }
        if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.TextureStreamingMeshUVChannelData) {
            uvChannelData = FMeshUVChannelInfo(Ar)
        }
    }
}

@OnlyAnnotated
class USkeletalMesh : UObject() {
    @F @UProperty("bHasVertexColors")
    var hasVertexColors = false
    lateinit var importedBounds: FBoxSphereBounds
    lateinit var materials: Array<FSkeletalMaterial>
    lateinit var referenceSkeleton: FReferenceSkeleton
    lateinit var lodModels: Array<FSkeletalMeshLODModel>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        val stripFlags = FStripDataFlags(Ar)
        importedBounds = FBoxSphereBounds(Ar)
        materials = Ar.readTArray { FSkeletalMaterial(Ar) }
        referenceSkeleton = FReferenceSkeleton(Ar)

        if (FSkeletalMeshCustomVersion.get(Ar) < FSkeletalMeshCustomVersion.SplitModelAndRenderData) {
            lodModels = Ar.readTArray { FSkeletalMeshLODModel() }
        } else {
            if (!stripFlags.isEditorDataStripped()) {
                lodModels = Ar.readTArray { FSkeletalMeshLODModel(Ar, this) }
            }
            val cooked = Ar.readBoolean()
            if (Ar.versions["SkeletalMesh.KeepMobileMinLODSettingOnDesktop"]) {
                var minMobileLODIdx = Ar.readInt32()
            }
            if (cooked) {
                val useNewCookedFormat = Ar.versions["SkeletalMesh.UseNewCookedFormat"]
                lodModels = Ar.readTArray {
                    val lodModel = FSkeletalMeshLODModel()
                    if (useNewCookedFormat) {
                        lodModel.serializeRenderItem(Ar, this)
                    } else {
                        lodModel.serializeRenderItemLegacy(Ar, this)
                    }
                    lodModel
                }
                if (useNewCookedFormat) {
                    var numInlinedLODs = Ar.read()
                    var numNonOptionalLODs = Ar.read()
                }
            }
        }
        if (Ar.ver < VER_UE4_REFERENCE_SKELETON_REFACTOR) {
            val length = Ar.readInt32()
            Ar.skip(12L * length) // TMap<FName, int32> DummyNameIndexMap
        }
        var dummyObjs = Ar.readTArray { FPackageIndex(Ar) }
    }
}