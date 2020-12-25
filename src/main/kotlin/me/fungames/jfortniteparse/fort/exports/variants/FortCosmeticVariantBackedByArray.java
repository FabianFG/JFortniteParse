package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FortCosmeticVariantBackedByArray extends FortCosmeticVariant {
    @Nullable
    public abstract List<? extends BaseVariantDef> getVariants();
}
