package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortMtxOfferData extends UPrimaryDataAsset {
    public FText DisplayName;
    public FText ShortDisplayName;
    public FText ShortDescription;
    public FText SubTitleText;
    public FText DisclaimerText;
    public List<FortItemQuantityPair> GrantOverride;
    public FSlateBrush TileImage;
    public FSlateBrush BadgeImage;
    public FSlateBrush DetailsImage;
    public FSlateBrush SimpleImage;
    public FPackageIndex /*UMaterialInterface*/ DetailsBadge;
    public List<FortMtxDetailsAttribute> DetailsAttributes;
    public FortMtxGradient Gradient;
    public FLinearColor Background;
    public FSlateBrush BackgroundImage;
    public FLinearColor UpsellPrimaryColor;
    public FLinearColor UpsellSecondaryColor;
    public FLinearColor UpsellTextColor;
    public Boolean bUseBaseColors;
    public EFortMtxOfferDisplaySize DisplaySize;

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
}
