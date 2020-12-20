package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;

public class FortRarityData extends UDataAsset {
    @UProperty(arrayDim = 8)
    public FortRarityItemData[] RarityCollection;

    @UStruct
    public static class FortRarityItemData {
        public FText Name;
        public FLinearColor Color1;
        public FLinearColor Color2;
        public FLinearColor Color3;
        public FLinearColor Color4;
        public FLinearColor Color5;
        public Float Radius;
        public Float Falloff;
        public Float Brightness;
        public Float Roughness;
        public Float Glow;
    }
}
