package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortMtxOfferData extends UPrimaryDataAsset {
    public FText Header;
    public FText DisplayName;
    public FText ShortDisplayName;
    public FText ShortDescription;
    public FText SubTitleText;
    public FText DisclaimerText;
    public List<FortItemQuantityPair> GrantOverride;
    public FSoftObjectPath SoftTileImage;
    public FVector2D TileImageSize;
    public FSoftObjectPath SoftBadgeImage;
    public FVector2D BadgeImageSize;
    public FSoftObjectPath SoftDetailsImage;
    public FVector2D DetailsImageSize;
    public FSoftObjectPath SoftSimpleImage;
    public FVector2D SimpleImageSize;
    public FPackageIndex /*UMaterialInterface*/ DetailsBadge;
    public List<FortMtxDetailsAttribute> DetailsAttributes;
    public FortMtxGradient Gradient;
    public FLinearColor Background;
    public FSoftObjectPath SoftBackgroundImage;
    public FVector2D BackgroundImageSize;
    public FLinearColor UpsellPrimaryColor;
    public FLinearColor UpsellSecondaryColor;
    public FLinearColor UpsellTextColor;
    public Boolean bUseBaseColors;
    public EFortMtxOfferDisplaySize DisplaySize;
    public List<FortMtxDescriptionAndDetails> DescriptionAndDetailsList;

    @UStruct
    public static class FortMtxDetailsAttribute {
        public FText Name;
        public FText Value;
    }

    @UStruct
    public static class FortMtxGradient {
        public FLinearColor Start;
        public FLinearColor Stop;
    }

    public enum EFortMtxOfferDisplaySize {
        Small,
        Medium,
        Large
    }

    @UStruct
    public static class FortMtxDescriptionAndDetails {
        public FText ShortDescription;
        public List<FortMtxDetailsAttribute> AssociatedDetailsAttributes;
    }
}
