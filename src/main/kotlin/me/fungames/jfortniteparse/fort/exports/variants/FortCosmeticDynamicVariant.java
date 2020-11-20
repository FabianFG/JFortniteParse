package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.DynamicVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticDynamicVariant extends FortCosmeticVariantBackedByArray {
    public List<DynamicVariantDef> DynamicOptions;

    @Nullable
    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return DynamicOptions;
    }
}
