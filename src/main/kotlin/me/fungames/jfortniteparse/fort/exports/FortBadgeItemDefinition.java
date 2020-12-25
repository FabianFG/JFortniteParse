package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortBadgeItemDefinition extends FortWorldItemDefinition {
    public FText DisplayText;
    public FText FailedDisplayText;
    public FLinearColor BadgeColor;
    public Integer UIMissionPointsOffset;
    public FDataTableRowHandle BadgeScoringValuesHandle;
    public FSoftObjectPath BadgeCardPackReward;
    public List<FortItemQuantityPair> ItemRewards;
}
