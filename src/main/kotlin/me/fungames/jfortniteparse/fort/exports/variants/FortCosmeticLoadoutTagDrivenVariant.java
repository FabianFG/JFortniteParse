package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.PartVariantDef;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticLoadoutTagDrivenVariant extends FortCosmeticVariantBackedByArray {
    public List<TagDrivenVariantDef> Variants;

    @Nullable
    @Override
    public List<TagDrivenVariantDef> getVariants() {
        return Variants;
    }

    public static class TagDrivenVariantDef extends PartVariantDef {
        public FGameplayTagContainer RequiredMetaTags;
        public FGameplayTagContainer ExcludedMetaTags;
    }
}
