package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortRewardInfo;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.List;

public class FortCollectionBookPageData extends FTableRowBase {
    public FText Name;
    public FName CategoryId;
    public int SortPriority;
    public FName ProfileId;
    public List<FName> SectionRowNames;
    public FortRewardInfo Rewards;
}
