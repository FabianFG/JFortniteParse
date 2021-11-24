package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.RichColorVariantDef;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

public class FortCosmeticRichColorVariant extends FortCosmeticVariant {
    public int AntiConflictChannel = -1;
    public EFortRichColorConflictResolutionRules AntiConflictRules = EFortRichColorConflictResolutionRules.BlackOrWhiteCannotConflict;
    public RichColorVariantDef InlineVariant;

	public FortCosmeticRichColorVariant() {
		VariantChannelTag = new FGameplayTag(new FName("Cosmetics.Variant.Channel.RichColor"));
		ActiveVariantTag = new FGameplayTag(new FName("Cosmetics.Variant.Property.RichColor"));
	}

    public enum EFortRichColorConflictResolutionRules {
        NoConflictsAllowed,
        BlackOrWhiteCannotConflict,
        EFortRichColorConflictResolutionRules_MAX
    }
}
