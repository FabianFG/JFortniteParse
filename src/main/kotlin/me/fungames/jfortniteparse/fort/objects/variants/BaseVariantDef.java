package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.fort.enums.EAttachmentRule;
import me.fungames.jfortniteparse.fort.enums.EFXType;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class BaseVariantDef {
    public Boolean bStartUnlocked;
    public Boolean bIsDefault;
    public Boolean bHideIfNotOwned;
    public FGameplayTag CustomizationVariantTag;
    public FText VariantName;
    public FSoftObjectPath PreviewImage;
    public FText UnlockRequirements;
    public FSoftObjectPath UnlockingItemDef;

    public String getBackendVariantName() {
        return CustomizationVariantTag != null ? CustomizationVariantTag.toString().substring("Cosmetics.Variant.Property.".length()) : null;
    }

    @UStruct
    public static class MeshVariant {
        public FSoftObjectPath MeshToSwap;
        public FName ComponentToOverride;
        public FSoftObjectPath OverrideMesh;
    }

    @UStruct
    public static class MaterialVariants {
        public FSoftObjectPath MaterialToSwap;
        public FName ComponentToOverride;
        public FName CascadeMaterialName;
        public Integer MaterialOverrideIndex;
        public FSoftObjectPath OverrideMaterial;
    }

    @UStruct
    public static class MaterialParamterDef {
        public FSoftObjectPath MaterialToAlter;
        public FName CascadeMaterialName;
        public List<MaterialVectorVariant> ColorParams;
        public List<MaterialTextureVariant> TextureParams;
        public List<MaterialFloatVariant> FloatParams;
    }

    @UStruct
    public static class VariantParticleSystemInitializerData {
        public FName ParticleComponentName;
        public FSoftObjectPath ParticleSystem;
        public FSoftObjectPath MeshToBindTO;
        public FName AttachSocketName;
        public EAttachmentRule LocationRule;
        public EAttachmentRule RotationRule;
        public EAttachmentRule ScaleRule;
        public Boolean bWeldSimulatedBodies;
    }

    @UStruct
    public static class ParticleVariant {
        public FSoftObjectPath ParticleSystemToAlter;
        public FName ComponentToOverride;
        public FSoftObjectPath OverrideParticleSystem;
    }

    @UStruct
    public static class ParticleParamterVariant {
        public FSoftObjectPath ParticleSystemToAlter;
        public List<MaterialVectorVariant> ColorParams;
        public List<VectorParamVariant> VectorParams;
        public List<MaterialFloatVariant> FloatParams;
    }

    @UStruct
    public static class ManagedParticleSwapVariant {
        public FGameplayTag ParamGroupTag;
        public FFortPortableSoftParticles ParticleToOverride;
    }

    @UStruct
    public static class FFortPortableSoftParticles {
        public EFXType FXType;
        public FSoftObjectPath NiagaraVersion;
        public FSoftObjectPath CascadeVersion;
    }

    @UStruct
    public static class ManagedParticleParamVariant {
        public FGameplayTag ParamGroupTag;
        public List<MaterialVectorVariant> ColorParams;
        public List<VectorParamVariant> VectorParams;
        public List<MaterialFloatVariant> FloatParams;
    }

    @UStruct
    public static class SoundVariant {
        public FSoftObjectPath SoundToSwap;
        public FName ComponentToOverride;
        public FSoftObjectPath OverrideSound;
    }

    @UStruct
    public static class FoleySoundVariant {
        public List<FPackageIndex /*FoleySoundLibrary*/> LibrariesToAdd;
        public List<FPackageIndex /*FoleySoundLibrary*/> LibrariesToRemove;
    }

    @UStruct
    public static class SocketTransformVariant {
        public FName SourceSocketName;
        public FName OverridSocketName;
        public FSoftObjectPath SourceObjectToModify;
    }

    @UStruct
    public static class ScriptedActionVariant {
        public FGameplayTag ActionTag;
    }

    @UStruct
    public static class CosmeticMetaTagContainer {
        public FGameplayTagContainer MetaTagsToApply;
        public FGameplayTagContainer MetaTagsToRemove;
    }
}
