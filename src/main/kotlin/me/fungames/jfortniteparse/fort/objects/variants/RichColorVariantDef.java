package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class RichColorVariantDef extends BaseVariantDef {
    public CosmeticMetaTagContainer MetaTags;
    public RichColorVariant RichColorVar;

    public RichColorVariantDef() {
        PreviewImage = new FSoftObjectPath(new FName("/Game/Characters/Fortress_Character/Textures/T_All_White.T_All_White"), "");
    }

    @UStruct
    public static class RichColorVariant {
        public static final FName NAME_PlayerChosenColor = new FName("PlayerChosenColor");

        public FLinearColor DefaultStartingColor = new FLinearColor(1.0f, 1.0f, 1.0f, 1.0f);
        public FSoftObjectPath ColorSwatchForChoices;
        public Boolean bVariantPickerShouldShowHSV = false;
        public List<FSoftObjectPath> MaterialsToAlter;
        public List<FSoftObjectPath> ParticlesToAlter;
        public FName ColorParamName = NAME_PlayerChosenColor;
    }
}
