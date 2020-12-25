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

    @UStruct
    public static class RichColorVariant {
        public FLinearColor DefaultStartingColor;
        public FSoftObjectPath ColorSwatchForChoices;
        public Boolean bVariantPickerShouldShowHSV;
        public List<FSoftObjectPath> MaterialsToAlter;
        public List<FSoftObjectPath> ParticlesToAlter;
        public FName ColorParamName;
    }
}
