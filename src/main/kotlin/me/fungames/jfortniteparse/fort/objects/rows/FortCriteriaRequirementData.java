package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;

public class FortCriteriaRequirementData extends FTableRowBase {
    public FGameplayTag RequiredTag;
    public boolean bGlobalMod;
    public float ModValue;
    public boolean bRequireRarity;
    public EFortRarity RequiredRarity;
}
