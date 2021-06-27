package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVChannelInfo
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FBoxSphereBounds
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.EPackageFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.versions.FEditorObjectVersion
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_MOVE_SKELETALMESH_SHADOWCASTING

class USkeletalMesh : UObject() {
    lateinit var bounds: FBoxSphereBounds
    lateinit var materials: Array<FSkeletalMaterial>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        val stripFlags = FStripDataFlags(Ar)
        bounds = FBoxSphereBounds(Ar)
        materials = Ar.readTArray { FSkeletalMaterial(Ar) }
    }
}

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
            //if (FCoreObjectVersion.get(Ar) >= FCoreObjectVersion.SkeletalMaterialEditorDataStripping) {
            serializeImportedMaterialSlotName = Ar.readBoolean()
            //}
            if (serializeImportedMaterialSlotName) {
                val importedMaterialSlotName = Ar.readFName()
            }
        } else {
            if (Ar.ver >= VER_UE4_MOVE_SKELETALMESH_SHADOWCASTING) {
                enableShadowCasting = true
            }
            //if (FRecomputeTangentCustomVersion.get(Ar) >= FRecomputeTangentCustomVersion.RuntimeRecomputeTangent) {
            val recomputeTangent = Ar.readBoolean()
            //}
        }
        if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.TextureStreamingMeshUVChannelData) {
            uvChannelData = FMeshUVChannelInfo(Ar)
        }
    }
}