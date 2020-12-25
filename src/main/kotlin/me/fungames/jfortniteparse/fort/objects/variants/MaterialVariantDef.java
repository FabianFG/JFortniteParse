package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class MaterialVariantDef extends BaseVariantDef {
    public List<MaterialVariants> VariantMaterials;
    public List<MaterialParamterDef> VariantMaterialParams;
    public List<SoundVariant> VariantSounds;
    public List<FoleySoundVariant> VariantFoley;
    public CosmeticMetaTagContainer MetaTags;
}
