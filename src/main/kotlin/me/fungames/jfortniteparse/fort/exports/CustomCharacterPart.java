package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortCustomBodyType;
import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.fort.enums.EFortCustomPartType;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class CustomCharacterPart extends UPrimaryDataAsset {
    public EFortCustomGender GenderPermitted;
    public EFortCustomBodyType BodyTypesPermitted;
    public EFortCustomPartType CharacterPartType;
    public FGameplayTagContainer GameplayTags;
    public FGameplayTagContainer DisallowedCosmeticTags;
    public Boolean bGameplayRelevantCosmeticPart;
    public Boolean bAttachToSocket;
    public Boolean bIgnorePart;
    public FSoftObjectPath /*SoftClassPath*/ PartModifierBlueprint;
    public FPackageIndex /*CustomCharacterPartData*/ AdditionalData;
    public FSoftObjectPath DefaultMontageLookupTable;
    public FSoftObjectPath OverrideMontageLookupTable;
    public FSoftObjectPath FrontendAnimMontageIdleOverride;
    public Float FrontEndBackPreviewRotationOffset;
    public FSoftObjectPath SkeletalMesh;
    public List<FSoftObjectPath> MasterSkeletalMeshes;
    public Boolean bSinglePieceMesh;
    public Boolean bSupportsColorSwatches;
    public Boolean bAllowStaticRenderPath;
    public List<CustomPartMaterialOverrideData> MaterialOverrides;
    public List<CustomPartTextureParameter> TextureParameters;
    public List<CustomPartScalarParameter> ScalarParameters;
    public List<CustomPartVectorParameter> VectorParameters;
    public List<FPackageIndex /*FoleySoundLibrary*/> FoleyLibraries;
    public Integer MaterialOverrideFlags;
    public FSoftObjectPath IdleEffect;
    public FSoftObjectPath IdleEffectNiagara;
    public FName IdleFXSocketName;
    public FPackageIndex /*MarshalledVFX_AuthoredDataConfig*/ AuthoredData;

    @UStruct
    public static class CustomPartMaterialOverrideData {
        public Integer MaterialOverrideIndex;
        public FSoftObjectPath OverrideMaterial;
    }

    @UStruct
    public static class CustomPartTextureParameter {
        public Integer MaterialIndexForTextureParameter;
        public FName TextureParameterNameForMaterial;
        public FSoftObjectPath TextureOverride;
    }

    @UStruct
    public static class CustomPartScalarParameter {
        public Integer MaterialIndexForScalarParameter;
        public FName ScalarParameterNameForMaterial;
        public Float ScalarOverride;
    }

    @UStruct
    public static class CustomPartVectorParameter {
        public Integer MaterialIndexForVectorParameter;
        public FName VectorParameterNameForMaterial;
        public FLinearColor VectorOverride;
    }
}
