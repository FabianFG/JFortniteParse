package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class ParticlVariantDef extends BaseVariantDef {
    public List<MaterialVariants> VariantMaterials;
    public List<MaterialParamterDef> VariantMaterialParams;
    public List<VariantParticleSystemInitializerData> InitalParticelSystemData;
    public List<ParticleVariant> VariantParticles;
    public List<ParticleParamterVariant> VariantParticleParams;
    public List<ManagedParticleParamVariant> VariantAlteredParticleParams;
    public List<SoundVariant> VariantSounds;
    public CosmeticMetaTagContainer MetaTags;
}
