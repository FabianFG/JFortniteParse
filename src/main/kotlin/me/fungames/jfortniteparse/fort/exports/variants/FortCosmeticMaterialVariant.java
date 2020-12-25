package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.MaterialVariantDef;

import java.util.List;

public class FortCosmeticMaterialVariant extends FortCosmeticVariantBackedByArray {
    public List<MaterialVariantDef> MaterialOptions;

    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return MaterialOptions;
    }
}
