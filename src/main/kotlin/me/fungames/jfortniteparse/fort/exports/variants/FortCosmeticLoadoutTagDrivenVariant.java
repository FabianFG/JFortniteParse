package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.PartVariantDef;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticLoadoutTagDrivenVariant extends FortCosmeticVariantBackedByArray {
    public List<TagDrivenVariantDef> Variants;

    public FortCosmeticLoadoutTagDrivenVariant() {
        VariantChannelTag = new FGameplayTag(new FName("Cosmetics.Variant.Channel.TagDriven")); // TODO check later
    }

    @Nullable
    @Override
    public List<TagDrivenVariantDef> getVariants() {
        return Variants;
    }

    @UStruct
    public static class TagDrivenVariantDef extends PartVariantDef {
        public FGameplayTagContainer RequiredMetaTags;
        public FGameplayTagContainer ExcludedMetaTags;
    }
}
