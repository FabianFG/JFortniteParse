package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.PartVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticCharacterPartVariant extends FortCosmeticVariantBackedByArray {
    public List<PartVariantDef> PartOptions;

    @Nullable
    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return PartOptions;
    }
}
