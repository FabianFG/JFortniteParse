package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCosmeticProfileBannerVariant extends FortCosmeticVariant {
    public static final FName NAME_Banner_Texture = new FName("Banner_Texture");
    public static final FName NAME_Banner_PrimaryColor = new FName("Banner_PrimaryColor");
    public static final FName NAME_Banner_SecondaryColor = new FName("Banner_SecondaryColor");
    public static final FName NAME_CC_PrimaryColor = new FName("CC_PrimaryColor");

    public List<FSoftObjectPath> MaterialsToAlter;
    public FName BannerIconParamName = NAME_Banner_Texture;
    public FName BannerPrimaryColorParamName = NAME_Banner_PrimaryColor;
    public FName BannerSecondaryColorParamName = NAME_Banner_SecondaryColor;
    public FName CC_PrimaryColorParamName = NAME_CC_PrimaryColor;

    public FortCosmeticProfileBannerVariant() {
        VariantChannelTag = new FGameplayTag(new FName("Cosmetics.Variant.Channel.ProfileBanner"));
        ActiveVariantTag = new FGameplayTag(new FName("Cosmetics.Variant.Property.ProfileBanner"));
    }
}
