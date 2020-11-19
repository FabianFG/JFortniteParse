package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortColorPalette;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortItemSeriesDefinition extends UPrimaryDataAsset {
    public FText DisplayName;
    public FortColorPalette Colors;
    public FSoftObjectPath BackgroundTexture;
    public FSoftObjectPath ItemCardMaterial;
    public FSoftObjectPath BackgroundMaterial;
}
