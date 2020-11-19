package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EAttachmentRule;
import me.fungames.jfortniteparse.fort.objects.*;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCosmeticParticleVariant extends FortCosmeticVariantBackedByArray {
    public List<ParticlVariantDef> ParticleOptions;

    @UStruct
    public static class ParticlVariantDef extends BaseVariantDef {
        public MaterialVariants[] VariantMaterials;
        public MaterialParamterDef[] VariantMaterialParams;
        public VariantParticleSystemInitializerData[] InitalParticelSystemData;
        public ParticleVariant[] VariantParticles;
        public ParticleParamterVariant[] VariantParticleParams;
        public ManagedParticleParamVariant[] VariantAlteredParticleParams;
        //        public Boolean bIndex7;
        public CosmeticMetaTagContainer MetaTags;
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
    public static class ManagedParticleParamVariant {
        public FGameplayTag ParamGroupTag;
        public List<MaterialVectorVariant> ColorParams;
        public List<VectorParamVariant> VectorParams;
        public List<MaterialFloatVariant> FloatParams;
    }
}
