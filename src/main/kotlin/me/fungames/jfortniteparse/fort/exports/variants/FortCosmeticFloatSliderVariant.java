package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

import static me.fungames.jfortniteparse.ue4.objects.uobject.FName.NAME_None;

public class FortCosmeticFloatSliderVariant extends FortCosmeticVariant {
    public float DefaultStartingValue = 0.0f;
    public float MinParamValue = 0.0f;
    public float MaxParamValue = 100.0f;
    public FName MaterialParamName = NAME_None;
    public List<FSoftObjectPath> MaterialsToAlter;

    public FortCosmeticFloatSliderVariant() {
        ActiveVariantTag = new FGameplayTag(new FName("Cosmetics.Variant.Property.FloatSlider"));
    }
}
