package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortLoginReward extends FTableRowBase {
    public FSoftObjectPath ItemDefinition;
    public int ItemCount;
    public FText Description;
    public boolean bIsMajorReward;
}
