package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class MeshVariantDef extends BaseVariantDef {
    public List<MeshVariant> VariantMeshes;
    public List<MaterialVariants> VariantMaterials;
    public List<MaterialParamterDef> VariantMaterialParams;
    public List<VariantParticleSystemInitializerData> InitialParticleSystemData;
    public List<ParticleVariant> VariantParticles;
    public List<ParticleParamterVariant> VariantParticleParams;
    public List<SocketTransformVariant> SocketTransforms;
    public List<SoundVariant> VariantSounds;
    public List<FoleySoundVariant> VariantFoley;
    public List<ScriptedActionVariant> VariantActions;
    public CosmeticMetaTagContainer MetaTags;
}
