package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

@UStruct
public class FortRewardInfo {
    public List<FortSelectableRewardOption> SelectableRewards;
    public List<FortItemQuantityPair> StandardRewards;
    public List<FortHiddenRewardQuantityPair> HiddenRewards;

    public boolean hasAnyRewards(boolean includeHidden) {
        return SelectableRewards.size() > 0 && StandardRewards.size() > 0 && (!includeHidden || HiddenRewards.size() > 0);
    }
}
