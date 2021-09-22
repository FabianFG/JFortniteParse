package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.MaterialVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticMaterialVariant extends FortCosmeticVariantBackedByArray {
    public List<MaterialVariantDef> MaterialOptions;

    @Nullable
    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return MaterialOptions;
    }
}
