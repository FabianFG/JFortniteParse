package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCosmeticFloatSliderVariant extends FortCosmeticVariant {
    public Float DefaultStartingValue;
    public Float MinParamValue;
    public Float MaxParamValue;
    public FName MaterialParamName;
    public List<FSoftObjectPath> MaterialsToAlter;
}
