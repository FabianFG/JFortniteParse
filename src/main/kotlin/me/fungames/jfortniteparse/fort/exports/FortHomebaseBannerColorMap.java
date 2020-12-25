package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.Map;

public class FortHomebaseBannerColorMap extends UDataAsset {
    public Map<FName, HomebaseBannerColor> ColorMap;

    @UStruct
    public static class HomebaseBannerColor {
        public FLinearColor PrimaryColor;
        public FLinearColor SecondaryColor;
    }
}
