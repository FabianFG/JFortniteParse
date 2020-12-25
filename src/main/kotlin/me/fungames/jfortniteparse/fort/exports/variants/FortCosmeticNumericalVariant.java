package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCosmeticNumericalVariant extends FortCosmeticVariant {
    public Integer DefaultStartingNumeric;
    public Integer MinNumericalValue;
    public Integer MaxNumbericalValue;
    public FName ZerosDigitParamName;
    public FName TensDigitParamName;
    public List<FSoftObjectPath> MaterialsToAlter;
}
