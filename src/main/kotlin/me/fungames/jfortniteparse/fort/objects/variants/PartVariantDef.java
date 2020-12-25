package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class PartVariantDef extends BaseVariantDef {
    public List<FSoftObjectPath> VariantParts;
    public List<MaterialVariants> VariantMaterials;
    public List<MaterialParamterDef> VariantMaterialParams;
    public List<VariantParticleSystemInitializerData> InitalParticelSystemData;
    public List<ParticleVariant> VariantParticles;
    public List<ParticleParamterVariant> VariantParticleParams;
    public List<ManagedParticleSwapVariant> VariantSwapInParticles;
    public List<ManagedParticleParamVariant> VariantAlteredParticleParams;
    public List<FoleySoundVariant> VariantFoley;
    public List<SocketTransformVariant> SocketTransforms;
    public List<ScriptedActionVariant> VariantActions;
    public CosmeticMetaTagContainer MetaTags;
}
