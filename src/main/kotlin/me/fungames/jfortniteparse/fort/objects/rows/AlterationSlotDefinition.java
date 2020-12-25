package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.List;

public class AlterationSlotDefinition extends FTableRowBase {
    public FName InitTierGroup;
    public List<FortItemQuantityPair> BaseRespecCosts;
}
