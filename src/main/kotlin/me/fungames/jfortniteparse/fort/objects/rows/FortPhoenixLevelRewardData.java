package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortRewardQuantityPair;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;

import java.util.List;

public class FortPhoenixLevelRewardData extends FTableRowBase {
    public int TotalRequiredXP;
    public boolean bIsMajorReward;
    public List<FortRewardQuantityPair> VisibleReward;
    public List<FortRewardQuantityPair> HiddenRewards;
    public String EventTag;
}
