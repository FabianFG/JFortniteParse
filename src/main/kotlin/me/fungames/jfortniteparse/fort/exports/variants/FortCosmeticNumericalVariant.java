package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCosmeticNumericalVariant extends FortCosmeticVariant {
    public static final FName NAME_Zero_Digit = new FName("Zero_Digit");
    public static final FName NAME_Ten_Digit = new FName("Ten_Digit");

    public int DefaultStartingNumeric = 14;
    public int MinNumericalValue = 0;
    public int MaxNumbericalValue = 99;
    public FName ZerosDigitParamName = NAME_Zero_Digit;
    public FName TensDigitParamName = NAME_Ten_Digit;
    public List<FSoftObjectPath> MaterialsToAlter;
}
